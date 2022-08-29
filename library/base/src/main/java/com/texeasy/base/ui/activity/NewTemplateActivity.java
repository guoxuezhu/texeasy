package com.texeasy.base.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.common.base.BaseActivity;
import com.example.common.base.BaseViewModel;
import com.example.common.utils.BeanUtils;
import com.texeasy.base.R;
import com.texeasy.base.databinding.ActivityNewTemplateBinding;


/**
 * Created by Jimmy on 2016/8/26 0026.
 */
public class NewTemplateActivity extends BaseActivity<ActivityNewTemplateBinding, BaseViewModel> implements View.OnClickListener {

    public static String NAME = "template.fragment.name";
    public static String TITLE = "template.title";
    public static String PARAMS = "template.fragment.params";

    private Fragment fragment;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_new_template;
    }

    @Override
    public int initVariableId() {
        return 0;
    }


    @Override
    public void initData() {
        initToolBar();
        initFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initFragment() {
        fragment = getSupportFragmentManager().findFragmentById(R.id.flTemplateContainer);
        if (fragment == null) {
            fragment = BeanUtils.getFragment(((Class) getIntent().getSerializableExtra(NAME)));
            Bundle params = getIntent().getBundleExtra(PARAMS);
            if (params != null) {
                fragment.setArguments(params);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.replace(R.id.flTemplateContainer, fragment);
            ft.commitAllowingStateLoss();
        }
    }

    private void initToolBar() {
        binding.tvTitle.setText("");
        binding.tvTitle.setText(getIntent().getStringExtra(TITLE));
        if (TextUtils.isEmpty(binding.tvTitle.getText())) {
            binding.ivLeft.setVisibility(View.INVISIBLE);
        } else {
            binding.ivLeft.setVisibility(View.VISIBLE);
        }
        binding.tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_back) {
            if (fragment != null && fragment instanceof NewTemplateFragment) {
                ((NewTemplateFragment) fragment).onRightButtonClick(v);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (fragment != null && fragment instanceof NewTemplateFragment) {
            ((NewTemplateFragment) fragment).onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
    }
}
