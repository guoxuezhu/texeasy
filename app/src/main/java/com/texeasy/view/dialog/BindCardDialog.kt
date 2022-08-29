package com.texeasy.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
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
import com.texeasy.base.ui.dialog.BaseLoadingDialog
import com.texeasy.base.widget.TwoButtonDialog
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.databinding.DialogBindCardBinding
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.HzHardwareProvider
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.CardBindReq
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 绑卡对话框
 */
class BindCardDialog(context: Context, var userName: String, var password: String) :
    HideNavigationBarDialog(context) {
    private var loadingDialog: BaseLoadingDialog? = null
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    val onCloseCommand = BindingCommand<Any>(::dismiss)

    init {
        initView()
    }

    private fun initView() {
        val binding: DialogBindCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_bind_card,
            null,
            false
        )
        binding.bindCardDialog = this
        setContentView(binding.root)
        getCardInfo()
    }

    fun getCardInfo() {
        // TODO: 2021/5/28 调硬件读卡
        HzHardwareManager.setCardReadCallBack { data, type ->
            if (data.isNotEmpty()) {
                Logcat.d(KLog.TAG+"卡片数据", data)
                val cardInfo = Gson().fromJson(data, CardInfo::class.java)
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        userBindIcCard(cardInfo.CardID)
                    }
                HzHardwareManager.stopReadCard()
            }
        }
        HzHardwareManager.startReadCard(object : PickerDialog.Action1<Boolean> {
            override fun call(t: Boolean) {
            }
        })
    }

    /**
     * 绑定IC卡接口设计
     */
    @SuppressLint("CheckResult")
    private fun userBindIcCard(cardID: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            ToastUtils.showShort("设备编码为空，请到设置中配置")
            dismiss()
            return
        }
        loadingDialog = BaseLoadingDialog(context)
        loadingDialog?.show()
        val cardBindReq = CardBindReq(userName, password, deviceCode, cardID)
        Injection.provideUserRepository().userBindIcCard(
            cardBindReq,
            object : OnResponseListener<Any>() {
                override fun onSuccess(data: Any?) {
                    ToastUtils.showShort("卡片绑定成功")
                    loadingDialog?.dismiss()
                    dismiss()
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    loadingDialog?.dismiss()
                    //结果描述【11:此卡已被绑定；12:用户已绑卡；13:绑定失败】
                    when (rspcode) {
                        "12" -> {
                            TwoButtonDialog(context).setDetailText("您已绑定过卡，是否重新绑定新卡替换旧卡")
                                .setRightButtonText(R.string.base_bind_new_card)
                                .setLeftListener {
                                    //重试
                                    getCardInfo()
                                }
                                .setListener {
                                    userTwoBindIcCard(cardBindReq)
                                }
                                .show()
                        }
                        else -> {
                            ToastUtils.showShort(rspmsg)
                            //重试
                            if (!isShowing) {
                                return
                            }
                            getCardInfo()
                        }
                    }
                }
            })
    }

    /**
     * 二次确认绑定IC卡接口设计
     */
    private fun userTwoBindIcCard(cardBindReq: CardBindReq) {
        loadingDialog?.show()
        Injection.provideUserRepository().userTwoBindIcCard(
            cardBindReq,
            object : OnResponseListener<Any>() {
                override fun onSuccess(data: Any?) {
                    ToastUtils.showShort("新卡绑定成功")
                    loadingDialog?.dismiss()
                    dismiss()
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    //结果描述【11:此卡已被绑定；12:用户已绑卡；13:绑定失败】
                    ToastUtils.showShort(rspmsg)
                    loadingDialog?.dismiss()
                    //重试
                    getCardInfo()
                }
            })
    }

    override fun dismiss() {
        super.dismiss()
        loadingDialog?.dismiss()
        HzHardwareManager.stopReadCard()
    }
}