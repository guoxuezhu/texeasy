package com.texeasy.base.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.view.dialog.HideNavigationBarDialog
import com.texeasy.base.R
import com.texeasy.base.databinding.BaseDialogLoadingBinding

class BaseLoadingDialog(context: Context, var content: String?) : HideNavigationBarDialog(context) {

    constructor(context: Context, listener: Listener) : this(context)

    constructor(context: Context) : this(context, null)


    var isStartRoundAnim = ObservableField(true)

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        var binding: BaseDialogLoadingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.base_dialog_loading,
            null,
            false
        )
        binding.viewModel = this
        setContentView(binding.root)
        binding.tvTip.text = content ?: return
    }

    override fun show() {
        isStartRoundAnim.set(true)
        super.show()
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