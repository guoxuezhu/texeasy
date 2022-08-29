package com.texeasy.view.fragment.basicsetting

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.example.common.widget.KeyboardChangeListener
import com.lazy.library.logging.Logcat
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.entity.BasicSettingInfo
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.base.utils.Helper
import com.texeasy.databinding.FragmentBasicSettingBinding

class BasicSettingFragment :
    NewTemplateFragment<FragmentBasicSettingBinding, BasicSettingViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_basic_setting
    }

    override fun initVariableId(): Int {
        return BR.ViewModel
    }

    override fun initData() {
        (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        KeyboardChangeListener(context as Activity).setKeyBoardListener { isShow, keyboardHeight ->
            viewModel.isShowKeyboard.set(isShow)
            Logcat.d(KLog.TAG, "isShow = [$isShow], keyboardHeight = [$keyboardHeight]")
        }
    }

    override fun initViewModel(): BasicSettingViewModel {
        val appViewModelFactory =
            AppViewModelFactory(BaseApplication.getInstance(), BasicSettingModel())
        return ViewModelProvider(this, appViewModelFactory).get(BasicSettingViewModel::class.java)
    }

    override fun initViewObservable() {
        //保存
        viewModel.uc.saveEvent.observe(this, Observer {
            val ipRegex =
                """(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])""".toRegex()
            val portRegex =
                """([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])""".toRegex()
            when {
                viewModel.serverIp.get().isNullOrEmpty() -> {
                    ToastUtils.showShort("ip不能为空")
                }
                viewModel.serverPort.get().isNullOrEmpty() -> {
                    ToastUtils.showShort("端口不能为空")
                }
                viewModel.deviceCode.get().isNullOrEmpty() -> {
                    ToastUtils.showShort("编号不能为空")
                }
                viewModel.socketKey.get().isNullOrEmpty() -> {
                    ToastUtils.showShort("密钥不能为空")
                }
                !ipRegex.matches(input = viewModel.serverIp.get()!!) -> {
                    ToastUtils.showShort("ip格式错误")
                }
                !portRegex.matches(input = viewModel.serverPort.get()!!) -> {
                    ToastUtils.showShort("端口格式错误")
                }
                else -> {
                    viewModel?.apply {
                        var basicSettingInfo = BasicSettingInfo(
                            serverIp.get()!!, serverPort.get()!!,
                            deviceCode.get()!!, socketKey.get()!!
                        )
                        ConfigCenter.updateBasicSettingInfo(basicSettingInfo)
                        Helper.restartApp(context ?: return@apply, R.string.base_config_modify_tip)
                    }
                }
            }
        })
    }

    override fun onRightButtonClick(v: View?) {
        if (viewModel.isShowKeyboard.get()!!) {
            Helper.hintKeyBoard(context)
        } else {
            super.onRightButtonClick(v)
        }
    }
}