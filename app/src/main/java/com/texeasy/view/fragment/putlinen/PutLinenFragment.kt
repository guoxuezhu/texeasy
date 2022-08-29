package com.texeasy.view.fragment.putlinen

//import com.texeasy.view.dialog.ScanLoadingDialog
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseApplication
import com.google.gson.Gson
import com.texeasy.BR
import com.texeasy.R
import com.texeasy.app.AppViewModelFactory
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.ui.activity.NewTemplateFragment
import com.texeasy.databinding.FragmentPutLinenBinding
import com.texeasy.repository.entity.UserAttestationResponse

/**
 * 上柜
 */
class PutLinenFragment : NewTemplateFragment<FragmentPutLinenBinding, PutLinenViewModel>() {
    var doorsInfo: List<String>? = null
    var userAttestationResponse: UserAttestationResponse? = null
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_put_linen
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initParam() {
        val userInfo = arguments?.getString(StringConstant.USER_ATTESTATION)
        userAttestationResponse = Gson().fromJson(userInfo, UserAttestationResponse::class.java)
        doorsInfo = userAttestationResponse?.userMessage?.doorCodes
    }

    override fun initViewModel(): PutLinenViewModel {
        val appViewModelFactory = AppViewModelFactory(BaseApplication.getInstance())
        val viewModel =
            ViewModelProvider(this, appViewModelFactory).get(PutLinenViewModel::class.java)
        viewModel.userAttestationResponse = userAttestationResponse
        return viewModel
    }

    override fun initData() {
//        userAttestationResponse?.let {
//            viewModel.openDoors(it)
//        }
    }

    override fun initViewObservable() {
        viewModel?.uc?.closeEvent?.observe(this, Observer {
            context?.let {
                (it as Activity).finish()
            }
        })
        viewModel?.uc?.cancelEvent?.observe(this, Observer {
            context?.let {

            }
        })
        viewModel?.uc?.confirmEvent?.observe(this, Observer {
            context?.let {

            }
        })
    }


    override fun onRightButtonClick(v: View?) {

    }
}