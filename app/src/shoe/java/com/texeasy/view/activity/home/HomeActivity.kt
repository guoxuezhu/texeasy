package com.texeasy.view.activity.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.common.base.BaseApplication
import com.example.common.bus.Messenger
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.google.gson.Gson
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.entity.CardInfo
import com.texeasy.base.ui.dialog.ScanLoadingDialog
import com.texeasy.base.utils.TimeUtil
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.face.FaceManager
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.CardAttestationReq
import com.texeasy.repository.entity.FingerAttestationReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.utils.AppHelper
import com.texeasy.view.adapter.ShoeStatisticsAdapter
import com.texeasy.view.dialog.FaceDialog
import com.texeasy.view.dialog.linenstatistics.LinenStatisticsDialog
import com.wellcom.FingerResult
import com.wellcom.GfpUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : BaseHomeActivity() {
    private val ACTION_REQUEST_CAMERA = 1
    private val NEEDED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )
    private var intentRFidService: Intent? = null
    private var shoeStatisticsAdapter: ShoeStatisticsAdapter? = null
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    var loadingDialog: ScanLoadingDialog? = null
    var fingerUtil: GfpUtils? = null
    var openFingerDisposable: Disposable? = null
    var timeCheckPressDisposable: Disposable? = null
    private var faceDialog: FaceDialog? = null
    private var isOpenFinger = false
    private var isOpenCard = false

    override fun initViewObservable() {
        super.initViewObservable()
        //??????????????????????????????
        viewModel.uc.startClothFragment.observe(this, Observer {
            LinenStatisticsDialog().show(supportFragmentManager, "LinenStatisticsDialog")
        })
        viewModel.doorInfosEvent.observe(this, Observer {
            if (shoeStatisticsAdapter == null) {
                shoeStatisticsAdapter = ShoeStatisticsAdapter(it)
                binding.homeCenterView.rvList?.adapter = shoeStatisticsAdapter
            } else {
                shoeStatisticsAdapter?.refreshData(it)
            }
        })

        viewModel.networkNameEvent.observe(this, Observer {
            when (it) {
                "ETHERNET" -> {
                    binding.homeToolbarView.ivNetworkStatus?.setBackgroundResource(R.mipmap.app_ic_net_ethernet)
                }
                "WIFI" -> {
                    binding.homeToolbarView.ivNetworkStatus?.setBackgroundResource(R.mipmap.app_ic_net_wifi_3)
                }
                "MOBILE" -> {
                    binding.homeToolbarView.ivNetworkStatus?.setBackgroundResource(R.mipmap.app_ic_net_signal_0)
                }
            }
        })
        viewModel?.uc?.scanLinenEvent?.observe(this, Observer {
            if (loadingDialog == null) {
                loadingDialog = ScanLoadingDialog(this)
            }
            if (loadingDialog?.isShowing == false) {
                loadingDialog?.show()
            }
        })
        viewModel?.uc?.closeEvent?.observe(this, Observer { error ->
            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
                //?????????????????????
                getCardInfo(true)
                timeCheckPress(true)
            }
            ToastUtils.showShort(error)
        })
        viewModel?.uc?.logoutEvent?.observe(this, Observer { error ->
            resetLoginView()
        })

        //????????????
        viewModel?.uc?.startFaceDialog?.observe(this, Observer {
            if (isLoading) {
                ToastUtils.showShort("???????????????")
                return@Observer
            }
            isLoading = true
            val isActivated = FaceManager.isActivated(this)
            if (!isActivated) {
                val properties: Properties? = FaceManager.loadProperties()
                if (properties == null) {
                    Logcat.e(KLog.TAG, "????????????????????????")
                    ToastUtils.showShort("????????????????????????")
                    isLoading = false
                    return@Observer
                }
                val appId: String = properties.getProperty("APP_ID") ?: ""
                val sdkKey: String = properties.getProperty("SDK_KEY") ?: ""
                val activeKey: String = properties.getProperty("ACTIVE_KEY") ?: ""
                disposable =
                    Observable.create(ObservableOnSubscribe<Int> { emitter ->
                        val activeOnline = FaceManager.activeOnline(this, activeKey, appId, sdkKey)
                        emitter.onNext(activeOnline)
                    }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            isLoading = false
                            when (result) {
                                0 -> ToastUtils.showShort("??????????????????")
                                90114 -> ToastUtils.showShort("?????????")
                                98309 -> ToastUtils.showShort("????????????????????????????????????")
                                else -> ToastUtils.showShort("?????????????????????????????????$result")
                            }
                        }
            } else {
                isLoading = false
                if (viewModel.isStopOperate) {
                    ToastUtils.showShort("????????????????????????????????????")
                    return@Observer
                }
                viewModel.isStopOperate = true
                faceDialog = FaceDialog(this, { data ->
                    BaseApplication.setAuthToken(data.authToken)
                    setUserAttestation2View(data)
                    AppHelper.openDoors(this@HomeActivity, data) { openDoorCodes ->
                        if (!openDoorCodes.isNullOrEmpty()) {
                            viewModel.checkDoorStatus(openDoorCodes)
                        } else {
                            viewModel.isStopOperate = false
                        }
                    }
                }) {
                    viewModel.isStopOperate = false
                }
                faceDialog?.show()
            }
        })
    }

    override fun initData() {
        ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_CAMERA)
        Messenger.getDefault().register(this, HzHardwareManager.CONNECT_SUCCESS) {
            getCardInfo()
            Messenger.getDefault().unregister(this)
        }
        super.initData()
        openFinger()
//        intentRFidService = Intent(this, RFidCheckService::class.java)
//        startService(intentRFidService)
    }

    fun openCard() {

    }

    @SuppressLint("CheckResult")
    fun getCardInfo(isRetry: Boolean = false) {
        if (!isOpenCard && isRetry) {
            return
        }
        if (isRetry) {
            resetLoginView()
        }
        HzHardwareManager.setCardReadCallBack(null)
        HzHardwareManager.setCardReadCallBack { data, type ->
            if (viewModel.isStopOperate) {
                return@setCardReadCallBack
            }
            viewModel.isStopOperate = true
            val cardInfo: CardInfo?
            if (data.isNotEmpty()) {
                Logcat.d(KLog.TAG + "????????????", data)
                try {
                    cardInfo = Gson().fromJson(data, CardInfo::class.java)
                } catch (e: Exception) {
                    Logcat.e(KLog.TAG, e.message)
                    Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                            ToastUtils.showShort("????????????????????????????????????")
                            viewModel.isStopOperate = false
                        }
                    return@setCardReadCallBack
                }
                if (cardInfo != null) {
                    userAttestationByCard(cardInfo.CardID)
                } else {
                    viewModel.isStopOperate = false
                }
            } else {
                viewModel.isStopOperate = false
            }
        }
        HzHardwareManager.startReadCard(object : PickerDialog.Action1<Boolean> {
            override fun call(t: Boolean) {
                isOpenCard = t
                if (!isOpenCard) {
                    Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                            ToastUtils.showShort("?????????????????????")
                        }
                }
            }
        })
    }

    /**
     * ??????????????????
     */
    @SuppressLint("CheckResult")
    private fun userAttestationByCard(cardID: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("??????????????????????????????????????????")
                    viewModel.isStopOperate = false
                }
            return
        }
        HzHardwareManager.stopReadCard()
        val cardAttestationReq = CardAttestationReq(cardID, deviceCode)
        Injection.provideUserRepository().userAttestationByCard(cardAttestationReq,
            object : OnResponseListener<UserAttestationResponse>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    if (data != null) {
                        BaseApplication.setAuthToken(data.authToken)
                        setUserAttestation2View(data)
                        AppHelper.openDoors(this@HomeActivity, data) { openDoorCodes ->
                            if (openDoorCodes.isNullOrEmpty()) {
                                viewModel.isStopOperate = false
                                getCardInfo(true)
                            } else {
                                viewModel.checkDoorStatus(openDoorCodes)
                            }
                        }
                    } else {
                        viewModel.isStopOperate = false
                        //??????
                        ToastUtils.showShort("????????????????????????")
                        getCardInfo(true)
                    }
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    Logcat.e(KLog.TAG, rspmsg)
                    ToastUtils.showShort("????????????????????????")
                    viewModel.isStopOperate = false
                    //??????
                    getCardInfo(true)
                }
            })
    }

    private fun setUserAttestation2View(data: UserAttestationResponse) {
        binding.homeCenterView.clOperate?.visibility = View.GONE
        binding.homeCenterView.clLoginInfo?.visibility = View.VISIBLE
        binding.homeCenterView.tvName.text = data.userMessage.userName
        binding.homeCenterView.tvJob?.text = data.userMessage.roleName
        binding.homeCenterView.tvLoginTime?.text =
            TimeUtil.getDateToString(TimeUtil.DEFAULT_DATE_FORMAT, System.currentTimeMillis())
    }

    private fun resetLoginView() {
        binding.homeCenterView.clOperate?.visibility = View.VISIBLE
        binding.homeCenterView.clLoginInfo?.visibility = View.GONE
    }

    @SuppressLint("CheckResult")
    private fun openFinger() {
        val MAX_TIP_COUNT = 5 //????????????5???
        var count = 0
        fingerUtil = GfpUtils(this)
        openFingerDisposable = Observable.interval(3000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                //0  ??????  ????????????
                isOpenFinger = fingerUtil?.openWelDevice() == 0
                if (!isOpenFinger) {
                    if (count < MAX_TIP_COUNT) {
                        Observable.just(true)
                            .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                ToastUtils.showShort("???????????????????????????????????????????????????")
                            }
                        count++
                    }
                } else {
                    Logcat.d(KLog.TAG, "??????????????????")
                    timeCheckPress(false)
                    openFingerDisposable?.dispose()
                }
            }
    }

    private fun timeCheckPress(isRetry: Boolean = false) {
        if (!isOpenFinger) {
            return
        }
        if (isRetry) {
            resetLoginView()
        }
        timeCheckPressDisposable?.dispose()
        timeCheckPressDisposable = null
        timeCheckPressDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                if (viewModel.isStopOperate) {
                    return@subscribe
                }
                val isPress = fingerUtil?.pressDetect() ?: false
                if (isPress) {
                    viewModel.isStopOperate = true
                    timeCheckPressDisposable?.dispose()
                    timeCheckPressDisposable = null
                    getFinger()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun getFinger() {
        val results = fingerUtil?.getFeature(2)
        if (results.isNullOrEmpty()) {
            viewModel.isStopOperate = false
            return
        }
        if (results.size < 2) {
            viewModel.isStopOperate = false
            return
        }
        val result = FingerResult(results[0].toInt(), data = results[1])
        val isSuccess: Boolean =
            result.code == 0 && !TextUtils.isEmpty(result.data)
        Logcat.d(
            KLog.TAG,
            if (isSuccess) "[HAL] ?????????????????????????????????????????????" + result.data else "[HAL] ??????????????????????????? "
        )
        if (isSuccess) {
            Logcat.d(KLog.TAG, "??????????????????\n??????????????????\n???????????????...")
            userAttestationByFinger(result.data)
        } else {
            viewModel.isStopOperate = false
            timeCheckPress(true)
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("??????????????????????????????")
                }
        }
    }

    /**
     * ??????????????????
     */
    @SuppressLint("CheckResult")
    private fun userAttestationByFinger(fingerprint: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("??????????????????????????????????????????")
                    viewModel.isStopOperate = false
                }
            timeCheckPress(true)
            return
        }
        val fingerAttestationReq = FingerAttestationReq(fingerprint, deviceCode)
        Injection.provideUserRepository().userAttestationByFinger(fingerAttestationReq,
            object : OnResponseListener<UserAttestationResponse>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    if (data != null) {
                        BaseApplication.setAuthToken(data.authToken)
                        setUserAttestation2View(data)
                        AppHelper.openDoors(this@HomeActivity, data) { openDoorCodes ->
                            if (openDoorCodes.isNullOrEmpty()) {
                                viewModel.isStopOperate = false
                                timeCheckPress(true)
                            } else {
                                viewModel.checkDoorStatus(openDoorCodes)
                            }
                        }
                    } else {
                        viewModel.isStopOperate = false
                        //??????
                        ToastUtils.showShort("????????????????????????")
                        timeCheckPress(true)
                    }
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    Logcat.e(KLog.TAG, rspmsg)
                    ToastUtils.showShort("????????????????????????")
                    viewModel.isStopOperate = false
                    //??????
                    timeCheckPress(true)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        //????????????????????????????????????????????????
        viewModel.isStopOperate = faceDialog?.isShowing == true
    }

    override fun onPause() {
        super.onPause()
        viewModel.isStopOperate = true
        if (isFinishing) {
            openFingerDisposable?.dispose()
            timeCheckPressDisposable?.dispose()
            fingerUtil?.closeWelDevice()
//            stopService(intentRFidService)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}