package com.example.common.view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.common.R;

/**
 * @author Zhihu
 */
public class LoadingDialog extends HideNavigationBarDialog {
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        setContentView(R.layout.common_dialog_loading);
    }
}
