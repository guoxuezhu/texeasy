package com.texeasy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.ui.dialog.BaseLoadingDialog
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.entity.BoxStatus
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.view.fragment.linen.LinenInfoFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by ZivChao on 2021/8/6
 * @author ZivChao
 */
object AppHelper {
    private const val MAX_CHECK_COUNT = 60L
    private const val CHECK_PERIOD = 1L

    /**
     * 打开柜门
     */
    @SuppressLint("CheckResult")
    fun openDoors(
        context: Context,
        data: UserAttestationResponse,
        listener: (MutableList<String>) -> Unit = {}
    ) {
        if (data.userMessage.doorCodes.isNullOrEmpty()) {
            ToastUtils.showShort("没有可以打开的柜门")
            listener(mutableListOf())
            return
        }
        val loadingDialog = BaseLoadingDialog(context, "请打开柜门，取走物品...")
        loadingDialog.show()
        val openDoorCodes = mutableListOf<String>()
        var checkDoorStatusDisposable: Disposable? = null
        checkDoorStatusDisposable = Observable.interval(0, CHECK_PERIOD, TimeUnit.SECONDS)
            .take(MAX_CHECK_COUNT)
            .subscribeOn(Schedulers.io())
            .subscribe {
                openDoorCodes.clear()
                for (doorCode in data.userMessage.doorCodes) {
                    val boxStatus = HzHardwareManager.getBoxStatus(doorCode)
                    if (boxStatus.openStatus == BoxStatus.OPEN) {
                        openDoorCodes.add(doorCode)
                    }
                }
                //所有柜门已打开，或者限制检查时间已到
                if (openDoorCodes.size == data.userMessage.doorCodes.size || it >= MAX_CHECK_COUNT - 1) {
                    checkDoorStatusDisposable?.dispose()
                    Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                            if (loadingDialog.isShowing) {
                                loadingDialog.dismiss()
                            }
                            Logcat.w(KLog.TAG, "检查柜门是否打开结束，数量为：${openDoorCodes.size} ")
                            openDoorInternal(openDoorCodes, listener, data, context)
                        }
                    return@subscribe
                }
                Logcat.w(KLog.TAG, "正在检查柜门是否打开，次数：$it , 数量为：${openDoorCodes.size}")
            }
        for (doorCode in data.userMessage.doorCodes) {
            HzHardwareManager.openBox(doorCode)
        }
    }

    private fun openDoorInternal(
        openDoorCodes: MutableList<String>,
        listener: (MutableList<String>) -> Unit,
        data: UserAttestationResponse,
        context: Context
    ) {
        var openDoorTip = ""
        if (openDoorCodes.size == 0) {
            ToastUtils.showShort("打开柜门失败")
            listener(mutableListOf())
            return
        }
        if (isShoeFlavor()) {
            listener(openDoorCodes)
        } else {
            for (door in openDoorCodes) {
                openDoorTip = "$openDoorTip$door,"
            }
            openDoorTip = if (!TextUtils.isEmpty(openDoorTip)) openDoorTip.substring(
                0,
                openDoorTip.length - 1
            ) else openDoorTip
            var title =
                if (data.userMessage.userName.isEmpty()) context.getString(R.string.base_linen_info_count)
                else "认证成功:${openDoorTip}号柜门已开 | 操作人:${data.userMessage.userName}"
            val bundle = Bundle()
            bundle.putStringArrayList(
                StringConstant.DOORS_INFO,
                openDoorCodes as ArrayList<String>?
            )
            NewTemplateUtils.startTemplate(
                context,
                LinenInfoFragment::class.java,
                title,
                bundle
            )
        }
    }

    /**
     * 是否是发鞋柜渠道
     */
    fun isShoeFlavor(): Boolean {
        return when (BaseApplication.getFlavor()) {
            "shoe" -> true
            else -> false
        }
    }
}