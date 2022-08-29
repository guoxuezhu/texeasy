package com.texeasy.view.fragment.linenindoor

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.common.base.BaseViewModel
import com.example.common.base.MultiItemViewModel
import com.example.common.binding.command.BindingAction
import com.example.common.binding.command.BindingCommand
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.http.OnResponseListener
import com.example.common.utils.ToastUtils
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.repository.cabinetlist.DeviceRepository
import com.texeasy.repository.entity.LinenInDoorInfo
import com.texeasy.repository.entity.LinenInDoorInfoReq
import me.tatarka.bindingcollectionadapter2.ItemBinding

@SuppressLint("CheckResult")
class LinenInDoorInfoViewModel(application: Application, var repository: DeviceRepository) :
    BaseViewModel<DeviceRepository>(application, repository) {

    private val HEAD = "head"
    private val CONTENT = "content"

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //确定
        var confirmEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    //给RecyclerView添加ObservableList
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    //RecyclerView多布局添加ItemBinding
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType) {
                HEAD -> {
                    itemBinding.set(BR.viewModel, R.layout.item_linen_in_door_info_head_view)
                }
                CONTENT -> {
                    itemBinding.set(BR.viewModel, R.layout.item_linen_in_door_info_content_view)
                }
            }
        }

    /**
     * 确定
     */
    val onConfirmCommand = BindingCommand<Any>(BindingAction {
        uc.confirmEvent.call()
    })

    fun setEpcCodes(epcCodes: List<String>) {
        val deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
        val linenInDoorInfoReq = LinenInDoorInfoReq(deviceCode, epcCodes)
        repository.editLinenInDoor(linenInDoorInfoReq, object :
            OnResponseListener<List<LinenInDoorInfo>>() {
            override fun onSuccess(data: List<LinenInDoorInfo>?) {
                if (data != null) {
                    val result = data.toMutableList()
                    result.add(0, LinenInDoorInfo())
                    for (i in result.indices) {
                        if (i == 0) {
                            val item =
                                LinenInDoorInfoListHeadViewModel(this@LinenInDoorInfoViewModel)
                            //条目类型为头布局
                            item.multiItemType(HEAD)
                            observableList.add(item)
                        } else {
                            val item =
                                LinenInDoorInfoListContentViewModel(
                                    this@LinenInDoorInfoViewModel,
                                    result[i]
                                )
                            //条目类型为内容布局
                            item.multiItemType(CONTENT)
                            observableList.add(item)
                        }
                    }
                }
            }

            override fun onError(rspcode: String?, rspmsg: String?) {
                ToastUtils.showShort(rspmsg)
            }

        })
    }
}