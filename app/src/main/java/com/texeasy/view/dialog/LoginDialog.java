package com.texeasy.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.common.binding.command.BindingCommand;
import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.R;
import com.texeasy.databinding.DialogLoginBinding;

/**
 * @author Zhihu
 */
public class LoginDialog extends HideNavigationBarDialog {
    public BindingCommand onShipperCommand = new BindingCommand(this::onShipper);
    public BindingCommand onAdminCommand = new BindingCommand(this::onAdmin);
    public BindingCommand onBasicSettingCommand = new BindingCommand(this::onBasicSetting);
    public BindingCommand onBindCardCommand = new BindingCommand(this::onBindCard);
    public BindingCommand onCloseCommand = new BindingCommand(this::onClose);

    public LoginDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        DialogLoginBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login, null, false);
        binding.setLoginDialog(this);
        setContentView(binding.getRoot());
    }

    private void onShipper() {
        new ShipperLoginDialog(context).show();
        dismiss();
    }

    private void onAdmin() {
        new AdminLoginDialog(context).show();
        dismiss();
    }

    private void onBasicSetting() {
        new DeviceLoginDialog(context).show();
        dismiss();
    }

    private void onBindCard() {
        new BindCardLoginDialog(context).show();
        dismiss();
    }

    private void onClose() {
        dismiss();
    }

}
