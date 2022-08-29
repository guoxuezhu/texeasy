package com.texeasy.view.fragment.rfid

import android.app.Application
import androidx.databinding.ObservableField
import com.example.common.base.BaseViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent

class RFidSettingViewModel(application: Application, rFidSettingModel: RFidSettingModel) :
    BaseViewModel<RFidSettingModel>(application, rFidSettingModel) {
    //是否显示键盘
    var isShowKeyboard = ObservableField(false)

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {

        //新增
        var addEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //保存
        var saveEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    /**
     * 新增
     */
    val onAddCommand = BindingCommand<Any>(BindingAction {
        uc.addEvent.call()
    })

    /**
     * 保存
     */
    val onSaveCommand = BindingCommand<Any>(BindingAction {
        uc.saveEvent.call()
    })
}