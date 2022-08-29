package com.texeasy.view.activity.home

import android.app.Application
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent

class HomeCenterViewModel(application: Application, homeCenterModel: HomeCenterModel) :
    BaseViewModel<HomeCenterModel>(application, homeCenterModel) {

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //读卡动画对话框
        var startCardDialog: SingleLiveEvent<Any> = SingleLiveEvent()

        //二维码动画对话框
        var startQrDialog: SingleLiveEvent<Any> = SingleLiveEvent()
    }

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

}