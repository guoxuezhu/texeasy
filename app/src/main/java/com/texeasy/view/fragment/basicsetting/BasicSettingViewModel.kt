package com.texeasy.view.fragment.basicsetting

import android.app.Application
import androidx.databinding.ObservableField
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.entity.BasicSettingInfo

class BasicSettingViewModel(application: Application, adminModel: BasicSettingModel) :
    BaseViewModel<BasicSettingModel>(application, adminModel) {
    //基础设置信息
    var basicSettingInfo: BasicSettingInfo = BasicSettingInfo()
    var serverIp = ObservableField("")
    var serverPort = ObservableField("")
    var deviceCode = ObservableField("")
    var socketKey = ObservableField("")
    //是否显示键盘
    var isShowKeyboard = ObservableField(false)
    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //保存
        var saveEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    /**
     * 保存
     */
    val onSaveCommand = BindingCommand<Any>(BindingAction {
        uc.saveEvent.call()
    })

    init {
        serverIp.set(ConfigCenter.newInstance().serverIp.get())
        serverPort.set(ConfigCenter.newInstance().serverPort.get())
        deviceCode.set(ConfigCenter.newInstance().deviceCode.get())
        socketKey.set(ConfigCenter.newInstance().socketKey.get())
    }
}