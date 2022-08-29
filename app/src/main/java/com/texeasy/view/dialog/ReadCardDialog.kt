package com.texeasy.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.base.BaseApplication
import com.example.common.binding.command.BindingCommand
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.example.common.view.dialog.HideNavigationBarDialog
import com.google.gson.Gson
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.entity.CardInfo
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.databinding.DialogReadCardBinding
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.HzHardwareProvider
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.CardAttestationReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.utils.AppHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 读卡对话框
 */
class ReadCardDialog(context: Context) : HideNavigationBarDialog(context) {
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    var time = ObservableField("0s")
    var countDownTimer: CountDownTimer? = null
    val onCloseCommand = BindingCommand<Any>(::dismiss)

    init {
        initView()
    }

    private fun initView() {
        val binding: DialogReadCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_read_card,
            null,
            false
        )
        binding.readCardDialog = this
        setContentView(binding.root)
        getCardInfo()
        countDownTimer = object : CountDownTimer(20 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time.set("${(millisUntilFinished / 1000).toInt()}s")
            }

            override fun onFinish() {
                time.set("0s")
                dismiss()
            }
        }
        countDownTimer?.start()
    }

    fun getCardInfo() {
        // TODO: 2021/5/28 调硬件读卡
        HzHardwareManager.setCardReadCallBack { data, type ->
            if (data.isNotEmpty()) {
                Logcat.d(KLog.TAG+"卡片数据", data)
                val cardInfo = Gson().fromJson(data, CardInfo::class.java)
                userAttestationByCard(cardInfo.CardID)
                HzHardwareManager.stopReadCard()
            }
        }
        HzHardwareManager.startReadCard(object : PickerDialog.Action1<Boolean> {
            override fun call(t: Boolean) {
            }
        })
    }

    /**
     * 后台验证卡号
     */
    @SuppressLint("CheckResult")
    private fun userAttestationByCard(cardID: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("设备编码为空，请到设置中配置")
                }
            dismiss()
            return
        }
        val cardAttestationReq = CardAttestationReq(cardID, deviceCode)
        Injection.provideUserRepository().userAttestationByCard(cardAttestationReq,
            object : OnResponseListener<UserAttestationResponse>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    data?.apply {
                        BaseApplication.setAuthToken(data.authToken)
                        dismiss()
                        AppHelper.openDoors(context, this)
                    }
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    ToastUtils.showShort(rspmsg)
                    //重试
                    if (!isShowing) {
                        return
                    }
                    getCardInfo()
                }
            })
    }

    override fun dismiss() {
        super.dismiss()
        countDownTimer?.cancel()
        HzHardwareManager.stopReadCard()
    }
}