package com.texeasy.view.activity.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseActivity
import com.example.common.base.BaseApplication
import com.example.common.utils.ToastUtils
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.databinding.ActivityHomeBinding
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.rfid.RFidManager
import com.texeasy.view.dialog.*
import com.texeasy.view.fragment.basicsetting.BasicSettingFragment
import com.texeasy.view.fragment.devicesetting.DeviceSettingFragment
import io.reactivex.disposables.Disposable

open class BaseHomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {
    var disposable: Disposable? = null
    var isLoading: Boolean = false
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_home
    }

    override fun initVariableId(): Int {
        return BR.HomeViewModel
    }

    override fun initViewModel(): HomeViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance(), HomeModel())
        return ViewModelProvider(this, appViewModelFactory).get(HomeViewModel::class.java)
    }

    override fun initData() {
        initHomOtherViewModel()
        initHardware()
    }

    fun initHardware() {
        if (HzHardwareManager.init(this)) {
            ToastUtils.showShort("驱动服务连接成功")
        } else {
            ToastUtils.showShort("驱动服务连接失败")
        }
        RFidManager.connectRs232(ConfigCenter.newInstance().comName.get()!!)
    }

    fun initHomOtherViewModel() {
        binding.homeCenterView.homeViewModel = viewModel
        binding.homeToolbarView.homeViewModel = viewModel
    }

    override fun initViewObservable() {
        //读卡监听
        viewModel?.uc?.startCardDialog?.observe(this, Observer {
            ReadCardDialog(this).show()
        })

        //二维码监听
        viewModel?.uc?.startQrDialog?.observe(this, Observer {
            QrScanDialog(this).show()
        })
        //指纹监听
        viewModel?.uc?.startFingerDialog?.observe(this, Observer {
            FingerDialog(this).show()
        })

        //设备管理
        viewModel?.uc?.startDeviceDialog?.observe(this, Observer {
            NewTemplateUtils.startTemplate(this, BasicSettingFragment::class.java, "")
        })

        //卡绑定
        viewModel?.uc?.startBindCardDialog?.observe(this, Observer {
            BindCardLoginDialog(this).show()
        })

        //管理员登录
        viewModel?.uc?.startAdminDialog?.observe(this, Observer {
            NewTemplateUtils.startTemplate(
                this,
                DeviceSettingFragment::class.java,
                this.getString(R.string.base_admin_admin_login)
            )
        })

        //配送管理
        viewModel?.uc?.startShipperDialog?.observe(this, Observer {
            ShipperLoginDialog(this).show()
        })

//        //设置页面
//        viewModel?.uc?.startAdminLoginEvent?.observe(this, Observer {
//            LoginDialog(this).show()
//        })
    }

    override fun onDestroy() {
        HzHardwareManager.release()
        RFidManager.release()
        super.onDestroy()
    }
}