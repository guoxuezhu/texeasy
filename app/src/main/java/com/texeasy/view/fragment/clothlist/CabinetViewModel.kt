package com.texeasy.view.fragment.clothlist

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.common.base.BaseViewModel
import com.example.common.base.MultiItemViewModel
import com.example.common.http.OnResponseListener
import com.example.common.utils.ToastUtils
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.repository.cabinetlist.DeviceRepository
import com.texeasy.repository.entity.DeviceInfoReq
import com.texeasy.repository.entity.DoorInfo
import me.tatarka.bindingcollectionadapter2.ItemBinding

@SuppressLint("CheckResult")
class CabinetViewModel(application: Application, repository: DeviceRepository) :
    BaseViewModel<DeviceRepository>(application, repository) {

    private val HEAD = "head"
    private val CONTENT = "content"

    //给RecyclerView添加ObservableList
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    init {
        //可以调用addSubscribe()添加Disposable，请求与View周期同步
        //模拟10个条目，数据源可以来自网络
        repository.getAllDoorInfo(DeviceInfoReq(ConfigCenter.newInstance().deviceCode.get()!!), object :
            OnResponseListener<List<DoorInfo>>() {
            override fun onSuccess(data: List<DoorInfo>?) {
                if (data != null) {
                    var result = data.toMutableList()
                    result.add(0, DoorInfo())
                    for (i in result.indices) {
                        if (i == 0) {
                            val item = CabinetListHeadViewModel(this@CabinetViewModel)
                            //条目类型为头布局
                            item.multiItemType(HEAD)
                            observableList.add(item)
                        } else {
                            val item = CabinetListContentViewModel(this@CabinetViewModel, result[i])
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

    //RecyclerView多布局添加ItemBinding
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType) {
                HEAD -> {
                    itemBinding.set(BR.viewModel, R.layout.item_cabinet_head_view)
                }
                CONTENT -> {
                    itemBinding.set(BR.viewModel, R.layout.item_cabinet_content_view)
                }
            }
        }
}