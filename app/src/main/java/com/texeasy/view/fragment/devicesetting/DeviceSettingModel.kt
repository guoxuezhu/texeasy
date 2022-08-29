package com.texeasy.view.fragment.devicesetting

import android.content.Context
import androidx.databinding.ObservableField
import com.example.common.base.BaseModel
import com.example.common.bus.event.SingleLiveEvent
import com.example.common.http.OnResponseListener
import com.example.common.utils.ToastUtils
import com.texeasy.base.R
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.AppVersionReq
import com.texeasy.repository.entity.AppVersionResp
import java.util.*

class DeviceSettingModel : BaseModel() {

    fun getComList(context: Context): List<String> {
        val timeList: MutableList<String> = ArrayList()
        timeList.add(context.getString(R.string.base_com_tty1))
        timeList.add(context.getString(R.string.base_com_tty2))
        timeList.add(context.getString(R.string.base_com_tty3))
        timeList.add(context.getString(R.string.base_com_tty4))
        timeList.add(context.getString(R.string.base_com_tty5))
        timeList.add(context.getString(R.string.base_com_tty6))
        timeList.add(context.getString(R.string.base_com_tty7))
        timeList.add(context.getString(R.string.base_com_tty8))
        return timeList
    }

    fun getDemoComList(context: Context): List<String> {
        val timeList: MutableList<String> = ArrayList()
        timeList.add(context.getString(R.string.base_com_tty1))
        timeList.add(context.getString(R.string.base_com_tty2))
        timeList.add(context.getString(R.string.base_com_tty3))
        timeList.add(context.getString(R.string.base_com_tty4))
        timeList.add(context.getString(R.string.base_com_ttymxc3))
        return timeList
    }

    /**
     * 设置语音播报
     */
    fun setVoiceStatus(status: SingleLiveEvent<Boolean>, isEnabled: ObservableField<Boolean>) {
        isEnabled.set(false)
        status.value = !status.value!!
        ToastUtils.showShort("设置语音播报成功：${status.value}")
        isEnabled.set(true)
    }

    /**
     * 设置设备自检
     */
    fun setSelfCheck(status: SingleLiveEvent<Boolean>, isEnabled: ObservableField<Boolean>) {
        isEnabled.set(false)
        status.value = !status.value!!
        ToastUtils.showShort("设置设备自检成功：${status.value}")
        isEnabled.set(true)
    }

    /**
     * 设置debug模式
     */
    fun setDebug(status: SingleLiveEvent<Boolean>, isEnabled: ObservableField<Boolean>) {
        isEnabled.set(false)
        status.value = !status.value!!
        ToastUtils.showShort("debug模式成功：${status.value} ，请点击保存")
        isEnabled.set(true)
    }

    /**
     * 设置设备自检
     */
    fun setCamera90(status: SingleLiveEvent<Boolean>, isEnabled: ObservableField<Boolean>) {
        isEnabled.set(false)
        status.value = !status.value!!
        ToastUtils.showShort("切换相机方向：${if (status.value == true) 90 else 270} ，请点击保存")
        isEnabled.set(true)
    }

    fun checkVer(versionReq: AppVersionReq, listener: OnResponseListener<AppVersionResp>) {
        Injection.provideCabinetRepository().checkVer(versionReq, listener)
    }
}