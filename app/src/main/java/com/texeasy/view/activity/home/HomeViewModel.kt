package com.texeasy.view.activity.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableField
import com.example.common.base.BaseApplication
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.http.NetworkUtil
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.utils.SPUtils
import com.example.common.utils.ToastUtils
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
import com.texeasy.repository.entity.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application, homeModel: HomeModel) :
    BaseViewModel<HomeModel>(application, homeModel) {
    private var disposable: Disposable? = null
    private var checkDoorStatusDisposable: Disposable? = null
    var isStopOperate: Boolean = false //是否停止操作，限制多种登录同时操作

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //跳转布草列表页
        var startClothFragment: SingleLiveEvent<Any> = SingleLiveEvent()

        //读卡
        var startCardDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //二维码
        var startQrDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //指纹
        var startFingerDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //人脸
        var startFaceDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //设备管理
        var startDeviceDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //卡绑定
        var startBindCardDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //管理员登录
        var startAdminDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //配送管理
        var startShipperDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //扫描布草
        var scanLinenEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //关闭扫描布草
        var closeEvent: SingleLiveEvent<String> = SingleLiveEvent()

        //登出
        var logoutEvent: SingleLiveEvent<String> = SingleLiveEvent()
    }

    /**
     * 设备信息
     */
    var equipmentName: ObservableField<String> = ObservableField("")

    /**
     * 设备名称
     */
    var deviceName: ObservableField<String> = ObservableField("智能发鞋柜")

    /**
     * 当前存储量
     */
    var currentStorage: ObservableField<Int> = ObservableField(0)

    /**
     * 占比
     */
    var percent: ObservableField<String> = ObservableField("")

    /**
     * 设备告警
     */
    var equipmentAlert: ObservableField<String> = ObservableField("")

    /**
     * 是否有告警
     */
    var isAlert: ObservableField<Boolean> = ObservableField(false)

    /**
     * 设备编号
     */
    var deviceNo: ObservableField<String> = ObservableField("")

    /**
     * 刷卡
     */
    val onCardCommand = BindingCommand<Any>(BindingAction {
        uc.startCardDialog.call()
    })

    /**
     * 二维码
     */
    val onQRCommand = BindingCommand<Any>(BindingAction {
        uc.startQrDialog.call()
    })

    /**
     * 指纹
     */
    val onFingerDialog = BindingCommand<Any>(BindingAction {
        uc.startFingerDialog.call()
    })

    /**
     * 人脸
     */
    val onFaceDialog = BindingCommand<Any>(BindingAction {
        uc.startFaceDialog.call()
    })

    /**
     * 设备管理
     */
    val onDeviceCommand = BindingCommand<Any>(BindingAction {
        uc.startDeviceDialog.call()
    })

    /**
     * 卡绑定
     */
    val onBindCardCommand = BindingCommand<Any>(BindingAction {
        uc.startBindCardDialog.call()
    })

    /**
     * 管理员登录
     */
    val onAdminCommand = BindingCommand<Any>(BindingAction {
        uc.startAdminDialog.call()
    })

    /**
     * 配送管理
     */
    val onShipperCommand = BindingCommand<Any>(BindingAction {
        uc.startShipperDialog.call()
    })

    /**
     * 跳转布草列表页
     */
    val onClothCommand = BindingCommand<Any>(BindingAction {
        uc.startClothFragment.call()
    })

    /**
     * 登出
     */
    val onLogoutCommand = BindingCommand<Any>(BindingAction {
        uc.logoutEvent.call()
    })

    var doorInfosEvent: SingleLiveEvent<MutableList<DoorInfo>> = SingleLiveEvent()
    var networkNameEvent: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        networkNameEvent.value = NetworkUtil.getNetworkType(application)
        val deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
        deviceNo = ConfigCenter.newInstance().deviceCode
        Injection.provideCabinetRepository().getDeviceInfo(DeviceInfoReq(deviceCode), object :
            OnResponseListener<DeviceInfo>() {
            override fun onSuccess(data: DeviceInfo?) {
                data?.apply {
                    BaseApplication.setOrgId(data.orgId)
                    BaseApplication.setFaceAppId(data.appId)
                    BaseApplication.setFaceSdkKey(data.sdkKey)
                    BaseApplication.setFaceActiveKey(data.activeKey)
                    //布草信息
                    getAllDoorInfo()
                    //心跳
                    keepAlive(deviceCode)
                    equipmentName.set("$departmentName - $deviceName")
                    this@HomeViewModel.deviceName.set(deviceName)
                    this@HomeViewModel.currentStorage.set(currentStorage)
                    if (totalStorage == 0) {
                        return@apply
                    }
                    val bg = BigDecimal((currentStorage.toDouble() / totalStorage.toDouble()) * 100)
                    val percent: Double = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    this@HomeViewModel.percent.set("$percent%")
                }
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                Logcat.e(KLog.TAG, rspmsg)
            }
        })
    }

    private fun keepAlive(deviceCode: String) {
        Injection.provideCabinetRepository().keepAlive(DeviceInfoReq(deviceCode), object :
            OnResponseListener<HeartbeatInfo>() {
            override fun onSuccess(data: HeartbeatInfo?) {
                data?.apply {
                    ConfigCenter.newInstance().isOpenVoice.set(data.isOpenVoiceReminder)
                    ConfigCenter.newInstance().isSelfCheck.set(data.isOpenSelfTest)
                    if (data.keepAliveTime <= 0) {
                        return
                    }
                    autoKeepAlive(data, deviceCode)
                }
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                Logcat.e(KLog.TAG, rspmsg)
            }
        })
    }

    fun autoKeepAlive(data: HeartbeatInfo, deviceCode: String) {
        disposable = Observable.interval(
            data.keepAliveTime.toLong(),
            data.keepAliveTime.toLong(),
            TimeUnit.SECONDS
        )
            .subscribeOn(Schedulers.io())
            .subscribe {
                Injection.provideCabinetRepository()
                    .keepAlive(DeviceInfoReq(deviceCode), object :
                        OnResponseListener<HeartbeatInfo>() {
                        override fun onSuccess(data: HeartbeatInfo?) {
                            data?.apply {
                                ConfigCenter.newInstance().isOpenVoice.set(data.isOpenVoiceReminder)
                                ConfigCenter.newInstance().isSelfCheck.set(data.isOpenSelfTest)
                                if (data.keepAliveTime <= 0) {
                                    if (disposable?.isDisposed == false) {
                                        disposable?.dispose()
                                    }
                                }
                            }
                        }

                        override fun onError(rspcode: String?, rspmsg: String?) {
                            Logcat.e(KLog.TAG, rspmsg)
                        }
                    })
            }
        addSubscribe(disposable)
    }

    private fun getAllDoorInfo() {
        Injection.provideCabinetRepository().getAllDoorInfo(
            DeviceInfoReq(ConfigCenter.newInstance().deviceCode.get()!!),
            object :
                OnResponseListener<List<DoorInfo>>() {
                override fun onSuccess(data: List<DoorInfo>?) {
                    doorInfosEvent.value = data?.toMutableList() ?: mutableListOf()
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    ToastUtils.showShort(rspmsg)
                }
            })
    }

    @SuppressLint("CheckResult")
    fun checkDoorStatus(doorsInfo: List<String>) {
        val closeDoorInfos = mutableListOf<String>()
        checkDoorStatusDisposable = Observable.interval(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                closeDoorInfos.clear()
                for (doorInfo in doorsInfo) {
                    val boxStatus = HzHardwareManager.getBoxStatus(doorInfo)
                    if (boxStatus.openStatus == BoxStatus.CLOSE) {
                        closeDoorInfos.add(doorInfo)
                    }
                }
                if (closeDoorInfos.size == doorsInfo.size) {
                    checkDoorStatusDisposable?.dispose()
                }
                if (checkDoorStatusDisposable?.isDisposed == true && closeDoorInfos.size == doorsInfo.size) {
                    Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                            uc.scanLinenEvent.call()
                        }
                    val json = SPUtils.getInstance().getString(StringConstant.RFID_INFO)
                    if (json.isNullOrEmpty()) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                uc.closeEvent.value = "请在设置页面配置柜门和天线"
                                isStopOperate = false
                            }
                        return@subscribe
                    }
                    val list: MutableList<RfidInfo> =
                        Gson().fromJson(json, object : TypeToken<List<RfidInfo>>() {}.type)
                    val antennaList = mutableListOf<AntennaInfo>()
                    val filterList = list.filter { doorsInfo.contains(it.door) }
                    if (filterList.isEmpty()) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                uc.closeEvent.value = "该柜门不存在"
                                isStopOperate = false
                            }
                        return@subscribe
                    }
                    for (rfidInfo in filterList) {
                        antennaList.add(
                            AntennaInfo(
                                rfidInfo.antenna.toByte(),
                                rfidInfo.power.toByte()
                            )
                        )

                    }
                    RFidManager.startRead(antennaList, object : RFidManager.RfidListener {
                        override fun callBack(
                            epcList: List<String>,
                            isTimeOut: Boolean
                        ) {
                            if (isTimeOut) {
                                editLinenInDoor(epcList)
                            }
                        }

                        override fun error(error: String) {
                            Observable.just(true)
                                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                    uc.closeEvent.value = error
                                    isStopOperate = false
                                }
                        }
                    })
                }
            }
        addSubscribe(checkDoorStatusDisposable)
    }

    fun editLinenInDoor(epcCodes: List<String>) {
        val deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
        val linenInDoorInfoReq = LinenInDoorInfoReq(deviceCode, epcCodes)
        Injection.provideCabinetRepository().editLinenInDoor(linenInDoorInfoReq, object :
            OnResponseListener<List<LinenInDoorInfo>>() {
            override fun onSuccess(data: List<LinenInDoorInfo>?) {
                uc.closeEvent.value = "上报成功"
                isStopOperate = false
                //刷新柜门数据
                getAllDoorInfo()
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                uc.closeEvent.value = rspmsg
                isStopOperate = false
            }
        })
    }
}