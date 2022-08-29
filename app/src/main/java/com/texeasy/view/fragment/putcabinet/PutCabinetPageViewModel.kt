package com.texeasy.view.fragment.putcabinet

import com.example.common.base.ItemViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.binding.command.BindingConsumer
import com.example.common.utils.ToastUtils
import com.texeasy.base.constant.PutCabinetPageType

class PutCabinetPageViewModel(viewModel: PutCabinetViewModel, @PutCabinetPageType type: Int) :
    ItemViewModel<PutCabinetViewModel>(viewModel) {
    /**
     * 帐号
     */
    var onAccountCommand = BindingCommand<String>(BindingConsumer {
        viewModel.uc.account.value = it
    })

    /**
     * 密码
     */
    var onPswCommand = BindingCommand<String>(BindingConsumer {
        viewModel.uc.password.value = it
    })

    /**
     * 登录
     */
    val onLoginCommand = BindingCommand<Any>(BindingAction {
        viewModel.userInfo.account = viewModel.uc.account.value ?: ""
        viewModel.userInfo.password = viewModel.uc.password.value ?: ""
        viewModel.uc.loginEvent.value = viewModel.userInfo
        ToastUtils.showShort(viewModel.userInfo.account + " : " + viewModel.userInfo.password)
    })
}