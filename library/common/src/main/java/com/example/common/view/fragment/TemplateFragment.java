package com.example.common.view.fragment;

import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.example.common.base.BaseFragment;
import com.example.common.base.BaseViewModel;

/**
 * Created by Jimmy on 2016/10/19 0019.
 */
public abstract class TemplateFragment<D extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragment<D, VM> {

    public void onLeftButtonClick(View v) {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    public void onRightButtonClick(View v) {

    }

    public void onBackPressed() {

    }
}
