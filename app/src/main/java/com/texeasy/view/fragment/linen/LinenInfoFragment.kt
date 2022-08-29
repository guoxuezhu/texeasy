package com.texeasy.view.fragment.linen

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.google.gson.Gson
import com.lazy.library.logging.Logcat
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.base.ui.dialog.ScanLoadingDialog
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.base.utils.TimeUtil
import com.texeasy.databinding.FragmentLinenInfoBinding
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.view.fragment.linenindoor.LinenInDoorInfoFragment
import java.util.*

/**
 * 当前柜内数量
 */
class LinenInfoFragment : NewTemplateFragment<FragmentLinenInfoBinding, LinenInfoViewModel>() {
    var doorsInfo: List<String>? = null
    var userInfo: String? = null
    var loadingDialog: ScanLoadingDialog? = null
    var isError = false
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_linen_info
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initParam() {
        doorsInfo = arguments?.getStringArrayList(StringConstant.DOORS_INFO)
        userInfo = arguments?.getString(StringConstant.USER_ATTESTATION)
    }

    override fun initViewModel(): LinenInfoViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance())
        return ViewModelProvider(this, appViewModelFactory).get(LinenInfoViewModel::class.java)
    }

    override fun initData() {
        isError = false
        viewModel.isShowConfirmBtn.set(isDepartmentFlavor())
        doorsInfo?.let {
            viewModel.setDoorsInfoReq(it)
            if (!isDepartmentFlavor()) {
                //启动柜门监测服务
                viewModel.checkDoorStatus(it)
            }
        }
    }

    override fun initViewObservable() {
        viewModel?.uc?.scanLinenEvent?.observe(this, Observer {
            context?.let {
                if (loadingDialog == null) {
                    loadingDialog = ScanLoadingDialog(it)
                    loadingDialog?.show()
                }
            }
        })
        viewModel?.uc?.closeEvent?.observe(this, Observer { error ->
            context?.let {
                if (loadingDialog?.isShowing == true) {
                    loadingDialog?.dismiss()
                }
                ToastUtils.showShort(error)
                isError = true
            }
        })
        viewModel?.uc?.startLinenInDoorInfoFragment?.observe(this, Observer { epcList ->
            var userAttestationResponse: UserAttestationResponse? =
                Gson().fromJson(userInfo, UserAttestationResponse::class.java)
            var title = context?.getString(R.string.app_linen_in_door_info)
            userAttestationResponse?.apply {
                title = "操作人:${userMessage.userName} | 操作时间:${
                    TimeUtil.getDateToString(
                        "yyyy/MM/dd HH:mm:ss",
                        System.currentTimeMillis()
                    )
                }"
            }
            var retList = mutableListOf<String>()
            if (epcList.isNullOrEmpty()) {
                Logcat.e(KLog.TAG, "上报成功：盘存失败")
            } else {
                retList = epcList.toMutableList()
            }
            val bundle = Bundle()
            bundle.putStringArrayList(StringConstant.EPC_CODES, retList as ArrayList<String>?)
            NewTemplateUtils.startTemplate(
                context,
                LinenInDoorInfoFragment::class.java,
                title,
                bundle
            )
            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }
            (context as Activity).finish()
        })

        viewModel?.uc?.confirmEvent?.observe(this, Observer {
            context?.let {
                (context as Activity).finish()
            }
        })
    }

    override fun onRightButtonClick(v: View?) {
        if (!isDepartmentFlavor()) {
            if (isError) {
                super.onRightButtonClick(v)
            }
        } else {
            super.onRightButtonClick(v)
        }
    }

    /**
     * 是否是科室渠道
     */
    fun isDepartmentFlavor(): Boolean {
        return when (BaseApplication.getFlavor()) {
            "department" -> true
            else -> false
        }
    }
}