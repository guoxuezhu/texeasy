package com.texeasy.app

import android.content.Context
import android.view.Gravity
import androidx.multidex.MultiDex
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.SizeUtil
import com.example.common.utils.ToastUtils
import com.tencent.bugly.crashreport.CrashReport
import com.texeasy.BuildConfig
import com.texeasy.base.constant.ConfigCenter

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        initData()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun initData() {
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //bugly
        CrashReport.initCrashReport(this, "2e437f12d3", BuildConfig.DEBUG)
        setVersionCode(BuildConfig.VERSION_CODE)
        setVersionName(BuildConfig.VERSION_NAME)
        setFlavor(BuildConfig.FLAVOR)
        setDebug(BuildConfig.DEBUG)
        ConfigCenter.getConfigInfo()
        //设置吐司位置
        ToastUtils.setGravity(Gravity.CENTER, 0, SizeUtil.dp2px(this, 30f).toInt());
        //logcat图形化工具
        if (BuildConfig.DEBUG) {
            val logcatManager = Class.forName("com.sandboxol.logcat.LogcatManager")
            val setFloatingWindow = logcatManager.getMethod(
                "setFloatingWindow",
                Boolean::class.java
            )
            setFloatingWindow.invoke(null, ConfigCenter.newInstance().isDebug.get())
            val method = logcatManager.getMethod("start")
            method.invoke(null)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}