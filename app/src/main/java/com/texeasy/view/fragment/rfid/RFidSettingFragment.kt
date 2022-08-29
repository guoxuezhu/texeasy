package com.texeasy.view.fragment.rfid

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.SPUtils
import com.example.common.utils.ToastUtils
import com.example.common.widget.KeyboardChangeListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lazy.library.logging.Logcat
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.base.entity.RfidInfo
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.base.utils.Helper
import com.texeasy.databinding.FragmentRfidSettingBinding
import com.texeasy.hardware.rfid.RFidManager


/**
 * 设备检查页面
 */
class RFidSettingFragment :
    NewTemplateFragment<FragmentRfidSettingBinding, RFidSettingViewModel>() {
    private var adapterBase: RfidRecycleAdapter? = null
    private var list = mutableListOf<RfidInfo>()
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_rfid_setting
    }

    override fun initVariableId(): Int {
        return BR.ViewModel
    }

    override fun initData() {
        val json = SPUtils.getInstance().getString(StringConstant.RFID_INFO)
        if (json.isNotEmpty()) {
            list = Gson().fromJson(json, object : TypeToken<List<RfidInfo>>() {}.type)
        }
        initRecycle()
        KeyboardChangeListener(context as Activity).setKeyBoardListener { isShow, keyboardHeight ->
            viewModel.isShowKeyboard.set(isShow)
            Logcat.d(KLog.TAG,"isShow = [$isShow], keyboardHeight = [$keyboardHeight]")
        }
    }

    override fun initViewModel(): RFidSettingViewModel {
        val appViewModelFactory =
            AppViewModelFactory(BaseApplication.getInstance(), RFidSettingModel())
        return ViewModelProvider(this, appViewModelFactory).get(RFidSettingViewModel::class.java)
    }

    override fun initViewObservable() {
        //新增
        viewModel.uc.addEvent.observe(this, Observer {
            adapterBase?.addData()
        })
        //保存
        viewModel.uc.saveEvent.observe(this, Observer {
            if (checkList()) {
                val toJson = Gson().toJson(list)
                SPUtils.getInstance().put(StringConstant.RFID_INFO, toJson)
                var antennas = list.map { AntennaInfo(it.antenna.toByte(), it.power.toByte()) }
                RFidManager.setWorkAntennaOutputPower(antennas)
                ToastUtils.showShort("保存成功")
            }
        })
    }

    private fun initRecycle() {
        //纵向滑动
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        //获取数据，向适配器传数据，绑定适配器
        adapterBase =
            RfidRecycleAdapter(context, list)
        binding.rvRfid.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = this@RFidSettingFragment.adapterBase
        }
    }

    private fun checkList(): Boolean {
        if (list.size > 0) {
            var firstInfo: RfidInfo? = null
            for (index in list.indices) {
                if (index == 0) {
                    firstInfo = list[index]
                }
                if (list[index].door.isEmpty() || list[index].antenna.isEmpty() || list[index].power.isEmpty()) {
                    ToastUtils.showShort("保存失败，请检查输入是否为空")
                    return false
                }
                try {
                    var toByteAntenna = list[index].antenna.toByte()
                } catch (e: NumberFormatException) {
                    ToastUtils.showShort("输入的天线必须是数字")
                    return false
                }
                try {
                    var toBytePower = list[index].power.toByte()
                    if (toBytePower < 0 || toBytePower > 33) {
                        ToastUtils.showShort("输入的功率必须是0-33之间")
                        return false
                    }
                } catch (e: NumberFormatException) {
                    ToastUtils.showShort("输入的功率必须是数字")
                    return false
                }
                if (list[index].power != firstInfo?.power) {
                    ToastUtils.showShort("保存失败，所有天线功率必须一样")
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     *设置天线功率
     */
    private fun setAntennaPower() {
        var map = HashMap<Byte, Byte>()
        if (list.size == 0) {
            return
        }
        for (info in list) {
            if (info.antenna.isNotEmpty() && info.power.isNotEmpty()) {
                map[info.antenna.toByte()] = info.power.toByte()
            }
        }
        RFidManager.stopRead()
    }

    override fun onRightButtonClick(v: View?) {
        if (viewModel.isShowKeyboard.get()!!) {
            Helper.hintKeyBoard(context)
        } else {
            super.onRightButtonClick(v)
        }
    }

}