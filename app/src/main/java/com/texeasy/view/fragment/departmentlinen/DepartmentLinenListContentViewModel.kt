package com.texeasy.view.fragment.departmentlinen

import androidx.databinding.ObservableField
import com.example.common.base.BaseViewModel
import com.example.common.base.MultiItemViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.utils.ToastUtils
import com.texeasy.repository.entity.CabinetInfo
import com.texeasy.repository.entity.DoorInfo

class DepartmentLinenListContentViewModel(viewModel: BaseViewModel<*>, var doorInfo: DoorInfo) :
    MultiItemViewModel<BaseViewModel<*>>(viewModel) {
    var cabinet: ObservableField<CabinetInfo> = ObservableField()

    //条目的点击事件
    var itemClick: BindingCommand<*> =
        BindingCommand<Any>(BindingAction { ToastUtils.showShort("我是内容布局") })

}