package com.texeasy.base.widget;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.base.R;

/**
 * @author Zhihu
 */
public class QrLoadingDialog extends HideNavigationBarDialog {
    public QrLoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        setContentView(R.layout.base_dialog_qr_loading);
    }
}
