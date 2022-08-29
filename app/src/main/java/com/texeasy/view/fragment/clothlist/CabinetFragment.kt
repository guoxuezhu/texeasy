package com.texeasy.view.fragment.clothlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.databinding.FragmentCabinetBinding

/**
 * 柜子列表
 */
class CabinetFragment : NewTemplateFragment<FragmentCabinetBinding, CabinetViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_cabinet
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): CabinetViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance())
        return ViewModelProvider(this, appViewModelFactory).get(CabinetViewModel::class.java)
    }

    override fun initData() {
        super.initData()
        //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象。
        // Adapter属于View层的东西, 不建议定义到ViewModel中绑定，以免内存泄漏
        //binding.adapter = BindingRecyclerViewAdapter<MultiItemViewModel<*>>()
    }
}