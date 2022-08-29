package com.texeasy.view.fragment.putlinen

import android.annotation.SuppressLint
import android.app.Application
import android.os.CountDownTimer
import androidx.databinding.ObservableField
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.Messenger
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.utils.SPUtils
import com.example.common.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lazy.library.logging.Logcat
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.constant.LinenOperationType
import com.texeasy.base.constant.MessengerConstant
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.base.entity.RfidInfo
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.entity.BoxStatus
import com.texeasy.hardware.rfid.RFidManager
import com.texeasy.hardware.rfid.reader.helper.InventoryBuffer
import com.texeasy.repository.cabinetlist.DeviceRepository
import com.texeasy.repository.entity.LinenOperationRecordsReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.repository.entity.UserMessage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class PutLinenViewModel(application: Application, var repository: DeviceRepository) :
    BaseViewModel<DeviceRepository>(application, repository) {
    var userAttestationResponse: UserAttestationResponse? = null
    var isStartRoundAnim = ObservableField(true)
    var isOpened = ObservableField(false)
    var isHasScanEpc = ObservableField(false)//是否扫描到布草
    var time = ObservableField("5s")
    var doorTime = ObservableField("5s")
    var ecpCount = ObservableField(0)
    var openDoorCodes = mutableListOf<String>()
    var errorDoorCodes = mutableListOf<String>()
    var countDownTimer: CountDownTimer? = null
    var doorCountDownTimer: CountDownTimer? = null
    var epcList: List<String>? = null
    var isCallOpenDoor: Boolean = false

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //跳转布草列表页
        var startLinenInDoorInfoFragment: SingleLiveEvent<InventoryBuffer> = SingleLiveEvent()

        //关闭扫描布草
        var closeEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //取消
        var cancelEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //确定
        var confirmEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    private var disposable: Disposable? = null

    /**
     * 取消
     */
    val onCancelCommand = BindingCommand<Any>(BindingAction {
        cancel()
        RFidManager.stopRead()
        initScan()
    })

    /**
     * 确定
     */
    val onConfirmCommand = BindingCommand<Any>(BindingAction {
        uc.confirmEvent.call()
        epcList?.let { openDoors(it) }
    })

    /**
     * 退出
     */
    val onCloseCommand = BindingCommand<Any>(BindingAction {
        uc.closeEvent.call()
    })

    /**
     * 打开柜门
     */
    fun openDoors(epcList: List<String>) {
        isCallOpenDoor = true
        userAttestationResponse?.apply {
            var userMessage: UserMessage = userMessage
            val doorCodes = userMessage.doorCodes.toMutableList()
            val filterDoorCodes = doorCodes.filterNot { openDoorCodes.contains(it) }
            for (doorCode in filterDoorCodes) {
                val isOpen = HzHardwareManager.openBox(doorCode)
                if (isOpen) {
                    initDoorTime()
                    openDoorCodes.add(doorCode)
                    val req = LinenOperationRecordsReq(
                        epcList, userMessage.userCode,
                        ConfigCenter.newInstance().deviceCode.get()!!, doorCode,
                        LinenOperationType.LINEN_PUT, System.currentTimeMillis().toString()
                    )
                    repository.linenCirculation(req, object : OnResponseListener<Any>() {
                        override fun onSuccess(data: Any?) {
                            ToastUtils.showShort("上柜成功")
                            Logcat.d(KLog.TAG,"上柜成功")
                        }

                        override fun onError(rspcode: String?, rspmsg: String?) {
                            ToastUtils.showShort(rspmsg)
                        }
                    })
                    break
                } else {
                    errorDoorCodes.add(doorCode)
                    Logcat.e(KLog.TAG,"${doorCode}门打开失败")
                }
            }
            if (errorDoorCodes.size > 0) {
                // TODO: 2021/6/13 记录和上报
                ToastUtils.showShort(errorDoorCodes.toString() + "门打开失败")
            }
        } ?: Observable.just(true)
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                uc.closeEvent.call()
            }

    }

    fun checkDoorStatus(doorInfo: String) {
        disposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                val boxStatus = HzHardwareManager.getBoxStatus(doorInfo)
                if (boxStatus.openStatus == BoxStatus.CLOSE) {
                    disposable?.dispose()
                    RFidManager.stopRead()
                }
            }
        addSubscribe(disposable)
    }

    init {
        initScan()
    }

    /**
     * 开始扫描
     */
    private fun initScan() {
        isStartRoundAnim.set(true)
        isHasScanEpc.set(false)
        scan()
        initTime()
    }

    /**
     * 扫描
     */
    private fun scan() {
        val json = SPUtils.getInstance().getString(StringConstant.RFID_INFO)
        if (json.isNullOrEmpty()) {
            ToastUtils.showShort("请在设置页面配置柜门和天线")
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    uc.closeEvent.call()
                }
            return
        }
        val list: MutableList<RfidInfo> =
            Gson().fromJson(json, object : TypeToken<List<RfidInfo>>() {}.type)
        val antennaList = mutableListOf<AntennaInfo>()
        for (rfidInfo in list) {
            antennaList.add(AntennaInfo(rfidInfo.antenna.toByte(), rfidInfo.power.toByte()))
        }
        //去重
        val distinctAntennaList = antennaList.distinctBy { it.antenna }
        //开始扫描
        RFidManager.startRead(distinctAntennaList, object : RFidManager.RfidListener {
            override fun callBack(epcList: List<String>, isTimeOut: Boolean) {
                if (epcList.isNotEmpty()) {
                    this@PutLinenViewModel.epcList = epcList
                    isHasScanEpc.set(true)
                    ecpCount.set(epcList.size)
                    if (isTimeOut) {
                        openDoors(epcList)
                    }
                } else {
                    if (isTimeOut) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                uc.closeEvent.call()
                            }
                    }
                }
            }

            override fun error(error: String) {
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort(error)
                        uc.closeEvent.call()
                    }
            }
        }, isRealTime = true)
    }

    /**
     * 扫描倒计时
     */
    private fun initTime() {
        countDownTimer = object : CountDownTimer(5 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time.set("${(millisUntilFinished / 1000).toInt()}s")
            }

            override fun onFinish() {
                time.set("0s")
                isStartRoundAnim.set(false)
            }
        }
        countDownTimer?.start()
    }

    /**
     * 开门后倒计时
     */
    private fun initDoorTime() {
        Observable.just(true)
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                isOpened.set(true)
                doorCountDownTimer = object : CountDownTimer(5 * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        doorTime.set("${(millisUntilFinished / 1000).toInt()}s")
                    }

                    override fun onFinish() {
                        doorTime.set("0s")
                        isStartRoundAnim.set(false)
                        doorCountDownTimer?.cancel()
                        uc.closeEvent.call()
                    }
                }
                doorCountDownTimer?.start()
            }
    }

    private fun cancel() {
        countDownTimer?.cancel()
        isStartRoundAnim.set(false)
        RFidManager.clearInventoryRealResult()
    }

    override fun onDestroy() {
        cancel()
        doorCountDownTimer?.cancel()
        Messenger.getDefault().send(openDoorCodes, MessengerConstant.REFRESH_DEPARTMENT_LINEN)
        super.onDestroy()
    }
}