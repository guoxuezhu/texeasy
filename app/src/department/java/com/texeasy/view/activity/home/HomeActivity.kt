package com.texeasy.view.activity.home

import androidx.lifecycle.Observer
import com.texeasy.view.dialog.linenstatistics.LinenStatisticsDialog

class HomeActivity : BaseHomeActivity() {

    override fun initViewObservable() {
        super.initViewObservable()
        //跳转柜子使用情况页面
        viewModel.uc.startClothFragment.observe(this, Observer {
            LinenStatisticsDialog().show(supportFragmentManager, "LinenStatisticsDialog")
        })

    }
}