package com.texeasy.view.fragment.departmentlinen

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
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
import com.texeasy.repository.entity.*
import io.reactivex.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*

@SuppressLint("CheckResult")
class DepartmentLinenViewModel(application: Application, var repository: DeviceRepository) :
    BaseViewModel<DeviceRepository>(application, repository) {
    var isClose = ObservableField(false)

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //上柜
        var putEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //退出
        var closeEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    private val HEAD = "head"
    private val CONTENT = "content"
    private var deviceInfoReq: DeviceInfoReq? = null
    private var disposable: Disposable? = null

    //给RecyclerView添加ObservableList
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    //RecyclerView多布局添加ItemBinding
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType) {
                HEAD -> {
                    itemBinding.set(BR.viewModel, R.layout.item_department_linen_head_view)
                }
                CONTENT -> {
                    itemBinding.set(BR.viewModel, R.layout.item_department_linen_content_view)
                }
            }
        }

    /**
     * 上柜
     */
    val onPutCommand = BindingCommand<Any>(BindingAction {
        uc.putEvent.call()
    })

    /**
     * 退出
     */
    val onCloseCommand = BindingCommand<Any>(BindingAction {
        uc.closeEvent.call()
    })

    init {
        setDoorsInfoReq()
    }

    fun setDoorsInfoReq() {
        deviceInfoReq = DeviceInfoReq(ConfigCenter.newInstance().deviceCode.get()!!)
        deviceInfoReq?.let { req ->
            repository.getAllDoorInfo(req, object :
                OnResponseListener<List<DoorInfo>>() {
                override fun onSuccess(data: List<DoorInfo>?) {
                    if (data != null) {
                        observableList.clear()
                        var result = data.toMutableList()
                        result.add(0, DoorInfo())
                        for (i in result.indices) {
                            if (i == 0) {
                                val item =
                                    DepartmentLinenListHeadViewModel(this@DepartmentLinenViewModel)
                                //条目类型为头布局
                                item.multiItemType(HEAD)
                                observableList.add(item)
                            } else {
                                val item =
                                    DepartmentLinenListContentViewModel(
                                        this@DepartmentLinenViewModel,
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

}