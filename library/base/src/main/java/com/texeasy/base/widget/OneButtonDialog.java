package com.texeasy.base.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.base.R;


public class OneButtonDialog extends HideNavigationBarDialog implements View.OnClickListener {

    private TextView tvDetails;
    private TextView btnSure;
    private boolean isAllow = true;
    private OneButtonDialogListener listener;

    public OneButtonDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public void initView() {
        setContentView(R.layout.base_dialog_one_button);
        tvDetails = findViewById(R.id.tvDetails);
        btnSure = findViewById(R.id.btnSure);
        btnSure.setOnClickListener(this);
    }

    public OneButtonDialog setButtonText(String buttonText) {
        btnSure.setText(buttonText);
        return this;
    }

    public OneButtonDialog setButtonText(int buttonTextResId) {
        btnSure.setText(buttonTextResId);
        return this;
    }

    public OneButtonDialog setDetailText(String detailText) {
        tvDetails.setText(detailText);
        return this;
    }

    public OneButtonDialog setDetailText(int detailTextResId) {
        tvDetails.setText(detailTextResId);
        return this;
    }

    public OneButtonDialog setListener(OneButtonDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public OneButtonDialog setAllowBackPress(boolean isAllow) {
        this.isAllow = isAllow;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSure) {
            if (listener != null) {
                listener.onClick();
            }
            dismiss();
        }
    }

    public interface OneButtonDialogListener {
        void onClick();
    }

    @Override
    public void onBackPressed() {
        if (isAllow) {
            super.onBackPressed();
        }
    }
}
