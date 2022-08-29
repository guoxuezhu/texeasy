package com.texeasy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.utils.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lazy.library.logging.Logcat
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.base.entity.RfidInfo
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.entity.BoxStatus
import com.texeasy.hardware.rfid.RFidManager
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.LinenInDoorInfo
import com.texeasy.repository.entity.LinenInDoorInfoReq
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class RFidCheckService : Service() {
    private var disposable: Disposable? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        check()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun check() {
        if (disposable != null && disposable?.isDisposed == false) {
            return
        }
        val localTime = ConfigCenter.newInstance().heartBeat.get()
        disposable = Observable.interval(1, localTime!!.toLong(), TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<Any?>() {
                override fun onStart() {
                    Logcat.d(KLog.TAG, "自检onStart")
                }

                override fun onNext(t: Any) {
                    if (localTime != 0 && localTime != ConfigCenter.newInstance().heartBeat.get()) {
                        disposable()
                        check()
                    } else {
                        Logcat.d(KLog.TAG, "自检onNext")
                        checkDoorAndUpload()
                    }
                }

                override fun onError(e: Throwable) {
                    Logcat.d(KLog.TAG, "自检onError")
                }

                override fun onComplete() {
                    Logcat.d(KLog.TAG, "自检onComplete")
                }
            })
    }

    /**
     * 检查柜门状态并且上报布草
     */
    fun checkDoorAndUpload() {
        val json = SPUtils.getInstance().getString(StringConstant.RFID_INFO)
        if (json.isNullOrEmpty()) {
            return
        }
        val list: MutableList<RfidInfo> =
            Gson().fromJson(json, object : TypeToken<List<RfidInfo>>() {}.type)
        if (list.isNullOrEmpty()) {
            return
        }
        val closeDoorInfos = mutableListOf<String>()
        closeDoorInfos.clear()
        for (rfidInfo in list) {
            val boxStatus = HzHardwareManager.getBoxStatus(rfidInfo.door)
            if (boxStatus.openStatus == BoxStatus.CLOSE) {
                closeDoorInfos.add(rfidInfo.door)
            }
        }
        val filterRfidInfoList =
            list.filter { closeDoorInfos.contains(it.door) }
        val antennaList =
            filterRfidInfoList.map { AntennaInfo(it.antenna.toByte(), it.power.toByte()) }
                .distinctBy { it.antenna }
        if (antennaList.isNullOrEmpty()) {
            Logcat.e(KLog.TAG, "自检startRead失败：antennaList.isNullOrEmpty()")
            return
        }
        RFidManager.startRead(antennaList, object : RFidManager.RfidListener {
            override fun callBack(epcList: List<String>, isTimeOut: Boolean) {
                if (isTimeOut) {
                    if (epcList.isNullOrEmpty()) {
                        return
                    }
                    editLinenInDoor(epcList)
                }
            }

            override fun error(error: String) {
                Logcat.e(KLog.TAG, "自检startRead失败：$error")
            }
        })
    }

    fun editLinenInDoor(epcCodes: List<String>) {
        val deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
        val linenInDoorInfoReq = LinenInDoorInfoReq(deviceCode, epcCodes)
        Injection.provideCabinetRepository().editLinenInDoor(linenInDoorInfoReq, object :
            OnResponseListener<List<LinenInDoorInfo>>() {
            override fun onSuccess(data: List<LinenInDoorInfo>?) {
                if (!data.isNullOrEmpty()) {
                    Logcat.d(KLog.TAG, "自检editLinenInDoor成功")
                }
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                Logcat.d(KLog.TAG, "自检editLinenInDoor失败：rspcode = $rspcode , rspmsg = $rspmsg")
            }
        })
    }

    override fun onDestroy() {
        disposable()
        super.onDestroy()
    }

    private fun disposable() {
        disposable?.apply {
            if (!isDisposed) {
                dispose()
                disposable = null
                Logcat.d(KLog.TAG, "自检销毁")
            }
        }
    }
}