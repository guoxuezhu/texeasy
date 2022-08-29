package com.texeasy.view.fragment.devicesetting

import android.app.Application
import androidx.databinding.ObservableField
import com.example.common.base.BaseApplication
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.binding.command.BindingConsumer
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.constant.HardwareConfig
import com.texeasy.repository.entity.AppVersionReq
import com.texeasy.repository.entity.AppVersionResp
import io.reactivex.rxkotlin.subscribeBy
import zlc.season.rxdownload4.download

class DeviceSettingViewModel(application: Application, var deviceSettingModel: DeviceSettingModel) :
    BaseViewModel<DeviceSettingModel>(application, deviceSettingModel) {
    var initialComValue: String? = null
    var isEnableVoiceBtn = ObservableField(true)//语音播报按键可点击状态
    var isEnableSelfCheckBtn = ObservableField(true)//设备自检按键可点击状态
    var isEnableDebugBtn = ObservableField(true)//debug模式按键可点击状态
    var isEnableCamera90Btn = ObservableField(true)//相机方向90度按键可点击状态
    var equipmentName = ObservableField("胸外科-南走廊-1号综合发放柜")
    var comName = ObservableField("/dev/ttyS4")
    var versionCode = ObservableField("${BaseApplication.getVersionCode()}")
    var versionName = ObservableField(BaseApplication.getVersionName())
    var checkId = ObservableField<Int>(R.id.rb_item_1)
    var isDownload = ObservableField<Boolean>(false)
    var rfidScanTime = ObservableField(5)

    //是否显示键盘
    var isShowKeyboard = ObservableField(false)

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //心跳
        var heartbeat: SingleLiveEvent<Int> = SingleLiveEvent()

        //语音
        var voiceStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

        //设备自检
        var selfCheckStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

        //debug模式
        var debugStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

        //相机方向90度
        var camera90: SingleLiveEvent<Boolean> = SingleLiveEvent()

        //rfid设置
        var rfidSettingEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //rfid com设置
        var comSettingEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //检查更新
        var updateEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //保存
        var saveEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    var onItemCheckCommand = BindingCommand<Int>(BindingConsumer {
        checkId.set(it)
        when (it) {
            R.id.rb_item_1 -> {
                uc.heartbeat.value = HardwareConfig.HEART_BEAT[0]
            }
            R.id.rb_item_2 -> {
                uc.heartbeat.value = HardwareConfig.HEART_BEAT[1]
            }
            R.id.rb_item_3 -> {
                uc.heartbeat.value = HardwareConfig.HEART_BEAT[2]
            }
            R.id.rb_item_4 -> {
                uc.heartbeat.value = HardwareConfig.HEART_BEAT[3]
            }
        }
    })

    /**
     * 语音播报
     */
    val onVoiceCommand = BindingCommand<Any>(BindingAction {
        model.setVoiceStatus(uc.voiceStatus, isEnableVoiceBtn)
    })

    /**
     * 设备自检
     */
    val onSelfCheckCommand = BindingCommand<Any>(BindingAction {
        model.setSelfCheck(uc.selfCheckStatus, isEnableSelfCheckBtn)
    })

    /**
     * debug模式
     */
    val onDebugCommand = BindingCommand<Any>(BindingAction {
        model.setDebug(uc.debugStatus, isEnableDebugBtn)
    })

    /**
     * 相机方向90度
     */
    val onCameraCommand = BindingCommand<Any>(BindingAction {
        model.setCamera90(uc.camera90, isEnableCamera90Btn)
    })

    /**
     * rfid设置
     */
    val onRFidSettingCommand = BindingCommand<Any>(BindingAction {
        uc.rfidSettingEvent.call()
    })

    /**
     * com设置
     */
    val onComSettingCommand = BindingCommand<Any>(BindingAction {
        uc.comSettingEvent.call()
    })

    /**
     * 检查更新
     */
    val onUpdateCommand = BindingCommand<Any>(BindingAction {
        uc.updateEvent.call()
    })

    /**
     * 保存
     */
    val onSaveCommand = BindingCommand<Any>(BindingAction {
        uc.saveEvent.call()
    })

    init {
        uc.heartbeat.value = ConfigCenter.newInstance().heartBeat.get()
        uc.voiceStatus.value = ConfigCenter.newInstance().isOpenVoice.get()
        uc.selfCheckStatus.value = ConfigCenter.newInstance().isSelfCheck.get()
        uc.debugStatus.value = ConfigCenter.newInstance().isDebug.get()
        uc.camera90.value = ConfigCenter.newInstance().camera90.get()
        comName.set(ConfigCenter.newInstance().comName.get())
        initialComValue = ConfigCenter.newInstance().comName.get()
        rfidScanTime.set(ConfigCenter.newInstance().rfidScanTime.get())
        when (uc.heartbeat.value) {
            HardwareConfig.HEART_BEAT[0] -> checkId.set(R.id.rb_item_1)
            HardwareConfig.HEART_BEAT[1] -> checkId.set(R.id.rb_item_2)
            HardwareConfig.HEART_BEAT[2] -> checkId.set(R.id.rb_item_3)
            HardwareConfig.HEART_BEAT[3] -> checkId.set(R.id.rb_item_4)
        }
    }

    fun checkVer() {
        isDownload.set(true)
        val versionReq = AppVersionReq(versionCode.get()!!, versionName.get()!!)
        deviceSettingModel.checkVer(versionReq, object : OnResponseListener<AppVersionResp>() {
            override fun onSuccess(data: AppVersionResp?) {
                if (data != null && data.versionUrl.isNotEmpty()) {
                    data.versionUrl.download()
                        .subscribeBy(
                            onNext = { progress ->
                                //download progress
                                if (progress.downloadSize == progress.totalSize) {
                                    isDownload.set(false)
                                    Logcat.d(
                                        KLog.TAG,
                                        "url = ${data.versionUrl} , progress : [totalSize = ${progress.totalSize},downloadSize = ${progress.downloadSize}]"
                                    )
                                }
                            },
                            onComplete = {
                                //download complete
                                isDownload.set(false)
                                Logcat.d(KLog.TAG, "url = ${data.versionUrl} , onComplete")
                            },
                            onError = {
                                //download failed
                                isDownload.set(false)
                                Logcat.d(
                                    KLog.TAG,
                                    "url = ${data.versionUrl} , onError = [${it.message}]"
                                )
                            }
                        )
                } else {
                    isDownload.set(false)
                }
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                isDownload.set(false)
            }
        })
    }
}