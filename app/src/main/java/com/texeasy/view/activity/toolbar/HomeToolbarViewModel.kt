package com.texeasy.view.activity.toolbar

import android.app.Application
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.utils.ToastUtils
import com.texeasy.base.constant.PageType

class HomeToolbarViewModel(application: Application, homeToolbarModel: HomeToolbarModel) :
    BaseViewModel<HomeToolbarModel>(application, homeToolbarModel) {

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //管理员登录页面
        var startAdminLoginEvent: SingleLiveEvent<Int> = SingleLiveEvent()

        //上柜
        var startCabinetEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    /**
     * 上柜
     */
    val onCabinetCommand = BindingCommand<Any>(BindingAction {
        uc.startCabinetEvent.call()
        ToastUtils.showShort("上柜")
    })

    /**
     * 设备设置
     */
    val onDeviceSettingCommand = BindingCommand<Any>(BindingAction {
        uc.startAdminLoginEvent.value = PageType.DEVICE_SETTINGS
        ToastUtils.showShort("设备设置")
    })

    /**
     * 基础设置
     */
    val onBasicSettingCommand = BindingCommand<Any>(BindingAction {
        uc.startAdminLoginEvent.value = PageType.BASIC_SETTINGS
        ToastUtils.showShort("基础配置")
    })
}