package com.texeasy.view.fragment.putcabinet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.example.common.view.fragment.TemplateFragment
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.databinding.FragmentPutCabinetBinding

/**
 * 上柜
 */
class PutCabinetFragment : TemplateFragment<FragmentPutCabinetBinding, PutCabinetViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_put_cabinet
    }

    override fun initVariableId(): Int {
        return BR.ViewModel
    }

    override fun initViewModel(): PutCabinetViewModel {
        val appViewModelFactory =
            AppViewModelFactory(BaseApplication.getInstance(), PutCabinetModel())
        return ViewModelProvider(this, appViewModelFactory).get(PutCabinetViewModel::class.java)
    }

    override fun initViewObservable() {
        //帐号
        viewModel.uc.account.observe(this, Observer {
//            if (it.isNullOrEmpty()) {
//                binding.vpContent.llAccount.error = "帐号不能为空"
//            } else {
//                binding.llAccount.error = ""
//            }
        })
        //密码
        viewModel.uc.password.observe(this, Observer {
//            if (it.isNullOrEmpty()) {
//                binding.llPsw.error = "密码不能为空"
//            } else if (it == "1") {
//                binding.llPsw.error = "密码错误"
//            } else {
//                binding.llPsw.error = ""
//            }
        })
        //登录
        viewModel.uc.loginEvent.observe(this, Observer {
//            binding.btnLogin.startAnimation()
//            Flowable.just(true).delay(1, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    when (pageType) {
//                        PageType.BASIC_SETTINGS -> TemplateUtils.startTemplate(
//                            context, BasicSettingFragment::class.java,
//                            context?.getString(com.texeasy.base.R.string.base_basic_setting)
//                        )
//                        PageType.DEVICE_SETTINGS -> TemplateUtils.startTemplate(
//                            context, DeviceSettingFragment::class.java,
//                            context?.getString(com.texeasy.base.R.string.base_device_setting)
//                        )
//                    }
//                    viewModel.finish()
//                }
        })
    }
}