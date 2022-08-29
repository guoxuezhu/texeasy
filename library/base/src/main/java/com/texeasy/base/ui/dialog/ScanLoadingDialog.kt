package com.texeasy.base.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.view.dialog.HideNavigationBarDialog
import com.texeasy.base.R
import com.texeasy.base.databinding.BaseDialogScanLoadingBinding

class ScanLoadingDialog(context: Context) : HideNavigationBarDialog(context) {

    constructor(context: Context, listener: Listener) : this(context)

    var isStartRoundAnim = ObservableField(true)

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        var binding: BaseDialogScanLoadingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.base_dialog_scan_loading,
            null,
            false
        )
        binding.viewModel = this
        setContentView(binding.root)
    }

    override fun dismiss() {
        isStartRoundAnim.set(false)
        super.dismiss()
    }

    override fun onBackPressed() {
        //do nothing
    }

    interface Listener {
        fun callBack()
    }
}