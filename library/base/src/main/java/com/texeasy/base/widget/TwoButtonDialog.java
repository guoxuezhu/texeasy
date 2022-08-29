package com.texeasy.base.widget;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.common.view.dialog.HideNavigationBarDialog;
import com.texeasy.base.R;

public class TwoButtonDialog extends HideNavigationBarDialog implements View.OnClickListener {

    private TextView tvTitle, tvDetails;
    private TextView btnSure, btnCancel;
    private OnTwoButtonDialogClickListener listener;
    private OnTwoButtonDialogLeftClickListener leftListener;

    public TwoButtonDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public View getRootView() {
        return findViewById(R.id.vLayout);
    }

    public void initView() {
        setContentView(R.layout.base_dialog_two_button);
        tvTitle = findViewById(R.id.tvTitle);
        tvDetails = findViewById(R.id.tvDetails);
        btnSure = findViewById(R.id.btnSure);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    public TwoButtonDialog setListener(OnTwoButtonDialogClickListener listener) {
        this.listener = listener;
        return this;
    }

    public TwoButtonDialog setLeftListener(OnTwoButtonDialogLeftClickListener leftListener) {
        this.leftListener = leftListener;
        return this;
    }

    public TwoButtonDialog setDetailText(int detailText) {
        tvDetails.setText(detailText);
        return this;
    }

    public TwoButtonDialog setDetailText(String detailText) {
        tvDetails.setText(Html.fromHtml(detailText));
        return this;
    }

    public TwoButtonDialog setTitleText(int titleText) {
        tvTitle.setText(titleText);
        return this;
    }

    public TwoButtonDialog setTitleText(String titleText) {
        tvTitle.setText(titleText);
        return this;
    }

    public TwoButtonDialog setRightButtonText(int rightButtonText) {
        btnSure.setText(rightButtonText);
        return this;
    }

    public TwoButtonDialog setLeftButtonText(int leftButtonText) {
        btnCancel.setText(leftButtonText);
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnCancel) {
            dismiss();
            if (leftListener != null) {
                leftListener.onLeftClick();
            }
        } else if (id == R.id.btnSure) {
            dismiss();
            if (listener != null) {
                listener.onClick();
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        if (leftListener != null) {
            leftListener.onLeftClick();
        }
    }

    public interface OnTwoButtonDialogClickListener {
        void onClick();
    }

    public interface OnTwoButtonDialogLeftClickListener {
        void onLeftClick();
    }
}
