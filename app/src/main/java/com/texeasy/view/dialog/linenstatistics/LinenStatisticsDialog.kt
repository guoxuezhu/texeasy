package com.texeasy.view.dialog.linenstatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.common.base.BaseApplication
import com.example.common.binding.command.BindingCommand
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.databinding.DialogLinenStatisticsBinding
import com.texeasy.repository.Injection

/**
 * 柜内布草统计
 */
class LinenStatisticsDialog() : DialogFragment() {
    private var linenStatisticsViewModel: LinenStatisticsViewModel? = null
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    val onCloseCommand = BindingCommand<Any>(::dismiss)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val view = initView()
        initViewObservable()
        return view
    }

    init {
        initViewObservable()
    }

    private fun initView(): View {
        val binding: DialogLinenStatisticsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_linen_statistics,
            null,
            false
        )
        linenStatisticsViewModel = LinenStatisticsViewModel(
            BaseApplication.getInstance(),
            Injection.provideCabinetRepository()
        )
        binding.viewModel = linenStatisticsViewModel
        return binding.root
    }

    private fun initViewObservable() {
        linenStatisticsViewModel?.uc?.closeEvent?.observe(this, Observer {
            dismiss()
        })
    }

    override fun dismiss() {
        super.dismiss()
    }
}