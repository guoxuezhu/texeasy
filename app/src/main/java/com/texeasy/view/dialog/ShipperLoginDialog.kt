package com.texeasy.view.dialog

import android.content.Context
import android.os.Bundle
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
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.CardInfo
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.base.utils.TimeUtil
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.databinding.DialogShipperLoginBinding
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.HzHardwareProvider
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.CardAttestationReq
import com.texeasy.repository.entity.PswAttestationReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.utils.AppHelper
import com.texeasy.view.fragment.departmentlinen.DepartmentLinenFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 配送员对话框
 */
class ShipperLoginDialog(context: Context) : HideNavigationBarDialog(context) {
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    private var account: String? = "sxAdmin"
    private var psw: String? = "123456"
    var isCardTab: ObservableField<Boolean> = ObservableField(true)
    var onAccountCommand = BindingCommand<String> { account: String? -> this.onAccount(account) }
    var onPswCommand = BindingCommand<String> { psw: String? -> this.onPsw(psw) }
    var onLoginCommand = BindingCommand<Any>(::pswLogin)
    var onCardTabCommand = BindingCommand<Any>(::onCardTab)
    var onPswTabCommand = BindingCommand<Any>(::onPswTab)
    var onCloseCommand = BindingCommand<Any>(::dismiss)

    init {
        initView()
        onCardTab()
    }

    private fun initView() {
        val binding: DialogShipperLoginBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_shipper_login,
            null,
            false
        )
        binding.shipperLoginDialog = this
        setContentView(binding.root)
    }

    private fun cardLogin() {
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

    private fun pswLogin() {
        if (TextUtils.isEmpty(deviceCode)) {
            ToastUtils.showShort("设备编码为空，请到设置中配置")
            return
        }
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort("帐号不能为空")
            return
        }
        if (TextUtils.isEmpty(psw)) {
            ToastUtils.showShort("密码不能为空")
            return
        }
        val req = PswAttestationReq(
            account!!, psw!!,
            ConfigCenter.newInstance().deviceCode.get()!!, "0"
        )
        Injection.provideUserRepository().userAttestationByPassword(req,
            object : OnResponseListener<UserAttestationResponse?>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    data?.apply {
                        BaseApplication.setAuthToken(data.authToken)
                        dismiss()
                        if (BaseApplication.getFlavor().equals("dirty")) {
                            AppHelper.openDoors(context, this)
                        } else {
                            openDoors(this)
                        }
                    }
                }

                override fun onError(rspcode: String, rspmsg: String) {
                    ToastUtils.showShort(rspmsg)
                }
            })
    }

    /**
     * 后台验证卡号
     */
    private fun userAttestationByCard(cardID: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("设备编码为空，请到设置中配置")
                }
            dismiss()
            return
        }
        val cardAttestationReq = CardAttestationReq(cardID, deviceCode, "0")
        Injection.provideUserRepository().userAttestationByCard(cardAttestationReq,
            object : OnResponseListener<UserAttestationResponse>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    data?.apply {
                        BaseApplication.setAuthToken(data.authToken)
                        dismiss()
                        if (BaseApplication.getFlavor().equals("dirty")) {
                            AppHelper.openDoors(context, this)
                        } else {
                            openDoors(this)
                        }
                    }
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    ToastUtils.showShort(rspmsg)
                    //重试
                    if (!isShowing) {
                        return
                    }
                    cardLogin()
                }
            })
    }

    /**
     * 打开柜门
     */
    private fun openDoors(data: UserAttestationResponse) {
        val bundle = Bundle()
        bundle.putString(StringConstant.USER_ATTESTATION, Gson().toJson(data))
        var title =
            if (data.userMessage.userName.isEmpty()) context.getString(R.string.base_linen_info_count)
            else "操作人:${data.userMessage.userName} | 操作时间:${
                TimeUtil.getDateToString(
                    "yyyy/MM/dd HH:mm:ss",
                    System.currentTimeMillis()
                )
            }"
        NewTemplateUtils.startTemplate(
            context,
            DepartmentLinenFragment::class.java,
            title,
            bundle
        )
    }

    private fun onAccount(account: String?) {
        this.account = account
    }

    private fun onPsw(psw: String?) {
        this.psw = psw
    }

    private fun onCardTab() {
        isCardTab.set(true)
        cardLogin()
    }

    private fun onPswTab() {
        isCardTab.set(false)
        HzHardwareManager.stopReadCard()
    }

    override fun dismiss() {
        super.dismiss()
        HzHardwareManager.stopReadCard()
    }
}