package com.texeasy.view.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.binding.command.BindingCommand
import com.example.common.view.dialog.HideNavigationBarDialog
import com.texeasy.R
import com.texeasy.databinding.DialogScanLoadingBinding

class ScanLoadingDialog(
    context: Context,
    var boxName: String = "",
    var listener: Listener? = null
) :
    HideNavigationBarDialog(context) {

    val onStopCommand = BindingCommand<Any>(::stop)
    val onCancelCommand = BindingCommand<Any>(::dismiss)
    var isStartRoundAnim = ObservableField(true)

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        var binding: DialogScanLoadingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_scan_loading,
            null,
            false
        )
        binding.viewModel = this
        setContentView(binding.root)
    }

    private fun stop() {
        listener?.callBack()
        dismiss()
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