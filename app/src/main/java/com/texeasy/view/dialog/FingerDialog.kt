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
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.databinding.DialogFingerBinding
import com.wellcom.FingerResult
import com.wellcom.GfpUtils
import com.texeasy.hardware.hzdongcheng.HzHardwareProvider
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.FingerAttestationReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.utils.AppHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 读卡对话框
 */
class FingerDialog(context: Context) : HideNavigationBarDialog(context) {
    private lateinit var binding: DialogFingerBinding
    var disposable: Disposable? = null
    var fingerUtil: GfpUtils =
        GfpUtils(context)
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    var time = ObservableField("0s")
    var countDownTimer: CountDownTimer? = null
    val onCloseCommand = BindingCommand<Any>(::dismiss)

    init {
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_finger,
            null,
            false
        )
        binding.fingerDialog = this
        setContentView(binding.root)
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
        timeCheckPress()
    }

    override fun show() {
        if (!openFinger()) {
            return
        }
        super.show()
    }

    private fun openFinger(): Boolean {
        //0  成功  其它失败
        val isOpen = fingerUtil.openWelDevice() == 0
        if (!isOpen) {
            ToastUtils.showShort("指纹打开失败，请重试")
            return false
        }
        return true
    }

    private fun timeCheckPress() {
        binding.tvTip.text = "请按下手指..."
        disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val isPress = fingerUtil.pressDetect()
                if (isPress) {
                    disposable?.dispose()
                    getFinger()
                }
                if (!isShowing) {
                    disposable?.dispose()
                }
            }
    }

    private fun getFinger() {
        binding.tvTip.text = "正在获取指纹\n请不要松开手指..."
        val results = fingerUtil.getFeature(2)
        val result = FingerResult(results[0].toInt(), data = results[1])
        val isSuccess: Boolean =
            result.code == 0 && !TextUtils.isEmpty(result.data)
        Logcat.d(
            KLog.TAG,
            if (isSuccess) "[HAL] 获取指纹特征码成功，特征值为：" + result.data else "[HAL] 获取指纹特征码失败 "
        )
        if (isSuccess) {
            binding.tvTip.text = "指纹获取成功\n可以松开手指\n正在校验中..."
            userAttestationByFinger(result.data)
        } else {
            dismiss()
            ToastUtils.showShort("指纹提取失败，请重试")
        }
    }

    /**
     * 后台验证卡号
     */
    @SuppressLint("CheckResult")
    private fun userAttestationByFinger(fingerprint: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("设备编码为空，请到设置中配置")
                }
            dismiss()
            return
        }
        val fingerAttestationReq = FingerAttestationReq(fingerprint, deviceCode)
        Injection.provideUserRepository().userAttestationByFinger(fingerAttestationReq,
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
                    timeCheckPress()
                }
            })
    }

    override fun dismiss() {
        super.dismiss()
        countDownTimer?.cancel()
        disposable?.dispose()
        fingerUtil.closeWelDevice()
    }
}