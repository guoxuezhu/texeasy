package com.texeasy.view.activity.home

import android.content.Intent
import androidx.lifecycle.Observer
import com.texeasy.service.RFidCheckService
import com.texeasy.view.dialog.linenstatistics.LinenStatisticsDialog

class HomeActivity : BaseHomeActivity() {
    private var intentRFidService: Intent? = null
    override fun initViewObservable() {
        super.initViewObservable()
        //跳转柜子使用情况页面
        viewModel.uc.startClothFragment.observe(this, Observer {
            LinenStatisticsDialog().show(supportFragmentManager, "LinenStatisticsDialog")
        })

    }

    override fun initData() {
        super.initData()
        intentRFidService = Intent(this, RFidCheckService::class.java)
        startService(intentRFidService)
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            stopService(intentRFidService)
        }
    }
}