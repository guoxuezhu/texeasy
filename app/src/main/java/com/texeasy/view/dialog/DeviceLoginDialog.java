package com.texeasy.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.common.binding.command.BindingCommand;
import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.R;
import com.texeasy.base.utils.NewTemplateUtils;
import com.texeasy.databinding.DialogDeviceLoginBinding;
import com.texeasy.view.fragment.basicsetting.BasicSettingFragment;

/**
 * @author Zhihu
 */
public class DeviceLoginDialog extends HideNavigationBarDialog {
    private String account, psw;
    public BindingCommand onLoginCommand = new BindingCommand(this::onLogin);
    public BindingCommand<String> onAccountCommand = new BindingCommand<>(this::onAccount);
    public BindingCommand<String> onPswCommand = new BindingCommand<>(this::onPsw);
    public BindingCommand onCloseCommand = new BindingCommand(this::onClose);

    public DeviceLoginDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        DialogDeviceLoginBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_device_login, null, false);
        binding.setDeviceLoginDialog(this);
        setContentView(binding.getRoot());
    }

    private void onLogin() {
//        if (TextUtils.isEmpty(account)) {
//            ToastUtils.showShort("帐号不能为空");
//            return;
//        }
//        if (TextUtils.isEmpty(psw)) {
//            ToastUtils.showShort("密码不能为空");
//            return;
//        }
//        if (!HardwareConfig.SETTING_ACCOUNT.equals(account)) {
//            ToastUtils.showShort("帐号错误");
//            return;
//        }
//        if (!HardwareConfig.SETTING_PSW.equals(psw)) {
//            ToastUtils.showShort("帐号错误");
//            return;
//        }
        NewTemplateUtils.startTemplate(context, BasicSettingFragment.class, "");
        dismiss();
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
