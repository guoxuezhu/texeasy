package com.texeasy.view.fragment.linen

import com.example.common.base.BaseViewModel
import com.example.common.base.MultiItemViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.utils.ToastUtils

class LinenInfoListHeadViewModel(viewModel: BaseViewModel<*>) :
    MultiItemViewModel<BaseViewModel<*>>(viewModel) {
    //条目的点击事件
    var itemClick: BindingCommand<*> =
        BindingCommand<Any>(BindingAction { ToastUtils.showShort("我是头布局") })
}