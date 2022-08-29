package com.texeasy.view.fragment.putcabinet

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.example.common.base.BaseViewModel
import com.example.common.bus.event.SingleLiveEvent
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.base.constant.PutCabinetPageType
import com.texeasy.base.entity.UserInfo
import me.tatarka.bindingcollectionadapter2.ItemBinding

class PutCabinetViewModel(application: Application, putCabinetModel: PutCabinetModel) :
    BaseViewModel<PutCabinetModel>(application, putCabinetModel) {
    var userInfo: UserInfo = UserInfo()

    //给ViewPager添加ObservableList
    var pageItems: ObservableList<PutCabinetPageViewModel> = ObservableArrayList()

    //给ViewPager添加ItemBinding
    var itemBinding: ItemBinding<PutCabinetPageViewModel> = ItemBinding.of(this::bindView)

    /**
     * viewpager id
     */
    var viewPageId = ObservableField<Int>(R.id.vp_content)

    /**
     * tab标题
     */
    var tabsTitleRes: ObservableList<Int> = ObservableArrayList<Int>()

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    class UIChangeObservable {
        //帐号
        var account: SingleLiveEvent<String> = SingleLiveEvent()

        //密码
        var password: SingleLiveEvent<String> = SingleLiveEvent()

        //管理员登录页面
        var loginEvent: SingleLiveEvent<UserInfo> = SingleLiveEvent()
    }

    init {
        tabsTitleRes.addAll(listOf(R.string.base_card_swipe, R.string.base_account_psw_login))
        initPageItems()
    }

    /**
     * 初始化pageViewModel
     */
    fun initPageItems() {
        pageItems.add(PutCabinetPageViewModel(this, PutCabinetPageType.CARD_LOGIN))
        pageItems.add(PutCabinetPageViewModel(this, PutCabinetPageType.ADMIN_LOGIN))
    }

    /**
     * 绑定view
     */
    private fun bindView(binding: ItemBinding<*>, position: Int, item: PutCabinetPageViewModel) {
        when (position) {
            0 -> binding.set(BR.viewModel, R.layout.item_put_cabinet_card_login_viewpager)
            1 -> binding.set(BR.viewModel, R.layout.item_put_cabinet_admin_login_viewpager)
        }
    }
}