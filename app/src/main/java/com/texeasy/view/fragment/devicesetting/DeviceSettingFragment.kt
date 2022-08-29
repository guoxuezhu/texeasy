package com.texeasy.view.fragment.devicesetting

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.example.common.widget.KeyboardChangeListener
import com.lazy.library.logging.Logcat
import com.texeasy.BR
import com.texeasy.BuildConfig
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.constant.HardwareConfig
import com.texeasy.base.entity.DeviceSettingInfo
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.base.utils.Helper
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.databinding.FragmentDeviceSettingBinding
import com.texeasy.view.fragment.rfid.RFidSettingFragment
import com.uhf.uhf.serialport.SerialPortFinder

/**
 * 设备检查页面
 */
class DeviceSettingFragment :
    NewTemplateFragment<FragmentDeviceSettingBinding, DeviceSettingViewModel>() {
    var pickerDialog: PickerDialog? = null
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_device_setting
    }

    override fun initVariableId(): Int {
        return BR.ViewModel
    }

    override fun initData() {
        binding.rbItem1.text = getString(R.string.base_heart_beat, HardwareConfig.HEART_BEAT[0])
        binding.rbItem2.text = getString(R.string.base_heart_beat, HardwareConfig.HEART_BEAT[1])
        binding.rbItem3.text = getString(R.string.base_heart_beat, HardwareConfig.HEART_BEAT[2])
        binding.rbItem4.text = getString(R.string.base_heart_beat, HardwareConfig.HEART_BEAT[3])
        KeyboardChangeListener(context as Activity).setKeyBoardListener { isShow, keyboardHeight ->
            viewModel.isShowKeyboard.set(isShow)
            Logcat.d(KLog.TAG, "isShow = [$isShow], keyboardHeight = [$keyboardHeight]")
        }
    }

    override fun initViewModel(): DeviceSettingViewModel {
        val appViewModelFactory =
            AppViewModelFactory(BaseApplication.getInstance(), DeviceSettingModel())
        return ViewModelProvider(this, appViewModelFactory).get(DeviceSettingViewModel::class.java)
    }

    override fun initViewObservable() {
//        //语音
//        viewModel.uc.voiceStatus.observe(this, Observer {
//            binding.
//        })
//        //语音
//        viewModel.uc.selfCheckStatus.observe(this, Observer {
//
//        })
        //rfid设置
        viewModel.uc.rfidSettingEvent.observe(this, Observer {
            NewTemplateUtils.startTemplate(context, RFidSettingFragment::class.java, "RFID设置")
        })
        //com设置
        viewModel.uc.comSettingEvent.observe(this, Observer {
            fragmentManager?.let {
                val inspectModel = viewModel.model as DeviceSettingModel
                var comList = SerialPortFinder().allDevicesPath.toList()
                if (comList.isNullOrEmpty()) {
                    comList = inspectModel.getComList(BaseApplication.getInstance())
                }
                pickerDialog = PickerDialog(comList, 3,
                    object : PickerDialog.Action1<Int> {
                        override fun call(t: Int) {
                            val positionToCom = comList[t]
                            viewModel.comName.set(positionToCom)
                            pickerDialog?.dismiss()
                        }
                    })
                pickerDialog?.show(it, pickerDialog?.tag)
            }
        })
        //检查更新
        viewModel.uc.updateEvent.observe(this, Observer {
            viewModel.checkVer()
        })
        //保存
        viewModel.uc.saveEvent.observe(this, Observer {
            val deviceSettingInfo = DeviceSettingInfo(
                viewModel.uc.heartbeat.value!!, viewModel.uc.voiceStatus.value!!,
                viewModel.uc.selfCheckStatus.value!!, viewModel.uc.debugStatus.value!!,
                viewModel.uc.camera90.value!!,
                viewModel.comName.get()!!, viewModel.rfidScanTime.get()!!
            )
            ConfigCenter.updateDeviceSettingInfo(deviceSettingInfo)
            //设置debug悬浮球
            if (BuildConfig.DEBUG) {
                val logcatManager = Class.forName("com.sandboxol.logcat.LogcatManager")
                val setFloatingWindow = logcatManager.getMethod(
                    "setFloatingWindow",
                    Boolean::class.java
                )
                setFloatingWindow.invoke(null, viewModel.uc.debugStatus.value == true)
            }
            if (!viewModel.comName.get().equals(viewModel.initialComValue)) {
                context?.let { context ->
                    Helper.restartApp(context, R.string.base_com_modify_tip)
                }
            } else {
                ToastUtils.showShort("保存成功")
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