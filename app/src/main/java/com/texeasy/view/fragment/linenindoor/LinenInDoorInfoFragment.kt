package com.texeasy.view.fragment.linenindoor

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.databinding.FragmentLinenInDoorInfoBinding

/**
 * 存取明细
 */
class LinenInDoorInfoFragment :
    NewTemplateFragment<FragmentLinenInDoorInfoBinding, LinenInDoorInfoViewModel>() {
    var epcCodes: List<String>? = null
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_linen_in_door_info
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initParam() {
        epcCodes = arguments?.getStringArrayList(StringConstant.EPC_CODES)
    }

    override fun initViewModel(): LinenInDoorInfoViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance())
        return ViewModelProvider(
            this,
            appViewModelFactory
        ).get(LinenInDoorInfoViewModel::class.java)
    }

    override fun initData() {
        epcCodes?.let {
            viewModel.setEpcCodes(it)
        }
    }

    override fun initViewObservable() {
        viewModel?.uc?.confirmEvent?.observe(this, Observer {
            context?.let {
                (context as Activity).finish()
            }
        })
    }
}