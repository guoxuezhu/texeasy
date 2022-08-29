package com.texeasy.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.common.base.BaseApplication;
import com.example.common.binding.command.BindingCommand;
import com.example.common.http.OnResponseListener;
import com.example.common.utils.ToastUtils;
import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.R;
import com.texeasy.base.constant.ConfigCenter;
import com.texeasy.databinding.DialogBindCardLoginBinding;
import com.texeasy.repository.Injection;
import com.texeasy.repository.entity.PswAttestationReq;
import com.texeasy.repository.entity.UserAttestationResponse;

/**
 * @author Zhihu
 */
public class BindCardLoginDialog extends HideNavigationBarDialog {
    private boolean isLoading = false;
    private String account, psw, deviceCode = ConfigCenter.newInstance().deviceCode.get();
    public BindingCommand onLoginCommand = new BindingCommand(this::onLogin);
    public BindingCommand<String> onAccountCommand = new BindingCommand<>(this::onAccount);
    public BindingCommand<String> onPswCommand = new BindingCommand<>(this::onPsw);
    public BindingCommand onCloseCommand = new BindingCommand(this::onClose);

    public BindCardLoginDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        DialogBindCardLoginBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_bind_card_login, null, false);
        binding.setBindCardLoginDialog(this);
        setContentView(binding.getRoot());
    }

    private void onLogin() {
        if (isLoading) {
            ToastUtils.showShort("正在登录，请稍候...");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort("帐号不能为空");
            return;
        }
        if (TextUtils.isEmpty(psw)) {
            ToastUtils.showShort("密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(deviceCode)) {
            ToastUtils.showShort("设备编码为空，请到设置中配置");
            return;
        }
        isLoading = true;
        PswAttestationReq pswAttestationReq = new PswAttestationReq(account, psw, deviceCode, "1");
        Injection.provideUserRepository().userAttestationByPassword(pswAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                BaseApplication.setAuthToken(data.getAuthToken());
                new BindCardDialog(context, account, psw).show();
                dismiss();
                isLoading = false;
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                ToastUtils.showShort(rspmsg);
                isLoading = false;
            }
        });
    }

    private void onAccount(String account) {
        this.account = account;
    }

    private void onPsw(String psw) {
        this.psw = psw;
    }

    private void onClose() {
        dismiss();
    }

}
