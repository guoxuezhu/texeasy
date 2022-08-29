package com.texeasy.view.fragment.linen

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
import com.example.common.utils.SPUtils
import com.example.common.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.base.entity.RfidInfo
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.entity.BoxStatus
import com.texeasy.hardware.rfid.RFidManager
import com.texeasy.repository.cabinetlist.DeviceRepository
import com.texeasy.repository.entity.DoorInfo
import com.texeasy.repository.entity.DoorInfoReq
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class LinenInfoViewModel(application: Application, var repository: DeviceRepository) :
    BaseViewModel<DeviceRepository>(application, repository) {
    var isShowConfirmBtn = ObservableField(false)

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //跳转布草列表页
        var startLinenInDoorInfoFragment: SingleLiveEvent<List<String>> = SingleLiveEvent()

        //扫描布草
        var scanLinenEvent: SingleLiveEvent<Any> = SingleLiveEvent()

        //关闭扫描布草
        var closeEvent: SingleLiveEvent<String> = SingleLiveEvent()

        //确定
        var confirmEvent: SingleLiveEvent<Any> = SingleLiveEvent()
    }

    private val HEAD = "head"
    private val CONTENT = "content"
    private var doorInfoReq: DoorInfoReq? = null
    private var disposable: Disposable? = null

    //给RecyclerView添加ObservableList
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    //RecyclerView多布局添加ItemBinding
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType) {
                HEAD -> {
                    itemBinding.set(BR.viewModel, R.layout.item_linen_info_head_view)
                }
                CONTENT -> {
                    itemBinding.set(BR.viewModel, R.layout.item_linen_info_content_view)
                }
            }
        }

    /**
     * 确定
     */
    val onConfirmCommand = BindingCommand<Any>(BindingAction {
        uc.confirmEvent.call()
    })

    fun setDoorsInfoReq(doorsInfo: List<String>) {
        doorInfoReq = DoorInfoReq(ConfigCenter.newInstance().deviceCode.get()!!, doorsInfo)
        doorInfoReq?.let { req ->
            repository.getDoorInfos(req, object :
                OnResponseListener<List<DoorInfo>>() {
                override fun onSuccess(data: List<DoorInfo>?) {
                    if (data != null) {
                        var result = data.toMutableList()
                        result.add(0, DoorInfo())
                        for (i in result.indices) {
                            if (i == 0) {
                                val item = LinenInfoListHeadViewModel(this@LinenInfoViewModel)
                                //条目类型为头布局
                                item.multiItemType(HEAD)
                                observableList.add(item)
                            } else {
                                val item =
                                    LinenInfoListContentViewModel(
                                        this@LinenInfoViewModel,
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

    fun checkDoorStatus(doorsInfo: List<String>) {
        val closeDoorInfos = mutableListOf<String>()
        disposable = Observable.interval(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                closeDoorInfos.clear()
                for (doorInfo in doorsInfo) {
                    val boxStatus = HzHardwareManager.getBoxStatus(doorInfo)
                    if (boxStatus.openStatus == BoxStatus.CLOSE) {
                        closeDoorInfos.add(doorInfo)
                    }
                }
                if (closeDoorInfos.size == doorsInfo.size) {
                    disposable?.dispose()
                }
                if (disposable?.isDisposed == true && closeDoorInfos.size == doorsInfo.size) {
                    Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                            uc.scanLinenEvent.call()
                        }
                    val json = SPUtils.getInstance().getString(StringConstant.RFID_INFO)
                    if (json.isNullOrEmpty()) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                uc.closeEvent.value = "请在设置页面配置柜门和天线"
                            }
                        return@subscribe
                    }
                    val list: MutableList<RfidInfo> =
                        Gson().fromJson(json, object : TypeToken<List<RfidInfo>>() {}.type)
                    val antennaList = mutableListOf<AntennaInfo>()
                    val filterList = list.filter { doorsInfo.contains(it.door) }
                    if (filterList.isEmpty()) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                uc.closeEvent.value = "该柜门不存在"
                            }
                        return@subscribe
                    }
                    for (rfidInfo in filterList) {
                        antennaList.add(
                            AntennaInfo(
                                rfidInfo.antenna.toByte(),
                                rfidInfo.power.toByte()
                            )
                        )

                    }
                    RFidManager.startRead(antennaList, object : RFidManager.RfidListener {
                        override fun callBack(
                            epcList: List<String>,
                            isTimeOut: Boolean
                        ) {
                            if (isTimeOut) {
                                Observable.just(true)
                                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                        uc.startLinenInDoorInfoFragment.value = epcList
                                    }
                            }
                        }

                        override fun error(error: String) {
                            Observable.just(true)
                                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                    uc.closeEvent.value = error
                                }
                        }
                    })
                }
            }
        addSubscribe(disposable)
    }
}