package com.texeasy.view.fragment.departmentlinen

//import com.texeasy.view.dialog.ScanLoadingDialog
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.example.common.bus.Messenger
import com.google.gson.Gson
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.MessengerConstant
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.base.utils.NewTemplateUtils
import com.texeasy.base.utils.TimeUtil
import com.texeasy.databinding.FragmentDepartmentLinenBinding
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.view.fragment.putlinen.PutLinenFragment


/**
 * 上柜
 */
class DepartmentLinenFragment :
    NewTemplateFragment<FragmentDepartmentLinenBinding, DepartmentLinenViewModel>() {
    var userInfo: String? = null
    var userAttestationResponse: UserAttestationResponse? = null
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_department_linen
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initParam() {
        userInfo = arguments?.getString(StringConstant.USER_ATTESTATION)
        userAttestationResponse = Gson().fromJson(userInfo, UserAttestationResponse::class.java)
        initMessenger()
    }

    override fun initViewModel(): DepartmentLinenViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance())
        return ViewModelProvider(
            this,
            appViewModelFactory
        ).get(DepartmentLinenViewModel::class.java)
    }

    override fun initViewObservable() {
        viewModel?.uc?.putEvent?.observe(this, Observer {
            context?.let {
                var title = it.getString(R.string.base_put_linen)
                userAttestationResponse?.apply {
                    title = "操作人:${userMessage.userName} | 操作时间:${
                        TimeUtil.getDateToString(
                            "yyyy/MM/dd HH:mm:ss",
                            System.currentTimeMillis()
                        )
                    }"
                }
                val bundle = Bundle()
                bundle.putString(StringConstant.USER_ATTESTATION, userInfo)
                NewTemplateUtils.startTemplate(it, PutLinenFragment::class.java, title, bundle)
            }
        })

        viewModel?.uc?.closeEvent?.observe(this, Observer {
            context?.let {
                (it as Activity).finish()
            }
        })
    }

    private fun initMessenger() {
        Messenger.getDefault().register(
            this,
            MessengerConstant.REFRESH_DEPARTMENT_LINEN,
            ArrayList::class.java
        ) { openDoorCodes ->
            //如果有打开柜门
            if (!openDoorCodes.isNullOrEmpty()) {
                //移除掉已经打开的柜门
                userAttestationResponse?.userMessage?.doorCodes?.removeAll(openDoorCodes)
                //如果剩余的能打开的柜门为空，修改按键为退出
                if (userAttestationResponse?.userMessage?.doorCodes?.size == 0) {
                    viewModel.isClose.set(true)
                } else {
                    viewModel.isClose.set(false)
                }
                userInfo = Gson().toJson(userAttestationResponse)
                viewModel.setDoorsInfoReq()
            }
        }
    }

    override fun onDestroy() {
        Messenger.getDefault().unregister(this)
        super.onDestroy()
    }
}