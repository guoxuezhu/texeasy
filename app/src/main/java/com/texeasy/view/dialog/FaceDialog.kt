package com.texeasy.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.base.BaseApplication
import com.example.common.binding.command.BindingCommand
import com.example.common.http.OnResponseListener
import com.example.common.utils.KLog
import com.example.common.view.dialog.HideNavigationBarDialog
import com.lazy.library.logging.Logcat
import com.texeasy.R
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.databinding.DialogFaceBinding
import com.texeasy.repository.Injection
import com.texeasy.repository.entity.FaceAttestationReq
import com.texeasy.repository.entity.UserAttestationResponse
import com.texeasy.utils.*
import com.texeasy.view.widget.FaceRectView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 人脸对话框
 */
class FaceDialog(
    context: Context,
    var listener: (UserAttestationResponse) -> Unit,
    var dismissListener: () -> Unit
) :
    HideNavigationBarDialog(context),
    OnGlobalLayoutListener {
    private lateinit var binding: DialogFaceBinding
    var deviceCode = ConfigCenter.newInstance().deviceCode.get()!!
    var time = ObservableField("")
    var tip = ObservableField("请您面向屏幕，开始刷脸")
    var name = ObservableField("")
    var job = ObservableField("")
    val onCloseCommand = BindingCommand<Any>(::onClose)
    val onSureCommand = BindingCommand<Any>(::onSure)
    private var rgbCameraHelper: DualCameraHelper? = null
    private var irCameraHelper: DualCameraHelper? = null
    private var rgbFaceRectTransformer: FaceRectTransformer? = null
    private val irFaceRectTransformer: FaceRectTransformer? = null

    private val previewConfig: PreviewConfig? = null
    private var liveModel: LivenessDetectViewModel? = null
    private var successCount = 0
    private var isSuccess: Boolean = false
    var isShowBackBtn = ObservableField(true)
    var isShowPic = ObservableField(false)

    init {
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_face,
            null,
            false
        )
        binding.faceDialog = this
        //保持亮屏
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)
        //在布局结束后才做初始化操作
        binding.dualCameraTexturePreviewRgb.viewTreeObserver.addOnGlobalLayoutListener(this)
        liveModel = LivenessDetectViewModel()
        liveModel?.init(context) {
            successCount++
            if (successCount > 10 && !isSuccess) {
                isSuccess = true
                isShowPic.set(true)
//                val base64FaceData = Base64.decode(it, Base64.DEFAULT)
//                binding.ivPic?.setImageBitmap(bytes2Bimap(base64FaceData))
                rgbCameraHelper?.stop()
                userAttestationByFace(it)
            }
        }
    }

    private fun bytes2Bimap(b: ByteArray): Bitmap? {
        return if (b.isNotEmpty()) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
//            toHorizontalMirror(bm)
        } else {
            null
        }
    }

    /**
     * 后台验证卡号
     */
    @SuppressLint("CheckResult")
    private fun userAttestationByFace(faceData: String) {
        if (TextUtils.isEmpty(deviceCode)) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    setErrorView("设备编码为空，请到设置中配置")
                }
            dismiss()
            return
        }
        setLoadingView()
        val faceAttestationReq = FaceAttestationReq(faceData, deviceCode)
        Injection.provideUserRepository().userAttestationByFace(faceAttestationReq,
            object : OnResponseListener<UserAttestationResponse>() {
                override fun onSuccess(data: UserAttestationResponse?) {
                    if (data != null) {
                        BaseApplication.setAuthToken(data.authToken)
                        setNormalViw("身份认证成功")
                        name.set(data.userMessage.userName)
                        job.set(data.userMessage.roleName)
                        Observable.timer(2, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                                listener(data)
                                dismiss()
                            }
                    } else {
                        setErrorData()
                    }
                }

                override fun onError(rspcode: String?, rspmsg: String?) {
                    Logcat.e(KLog.TAG, rspmsg)
                    setErrorData()
                }
            })
    }

    private fun setLoadingView() {
        isShowBackBtn.set(false)
        setNormalViw("正在认证中，请稍候")
    }

    private fun setErrorData() {
        successCount = 0
        isSuccess = false
        isShowBackBtn.set(true)
        rgbCameraHelper?.start()
        setErrorView("认证失败，请重拍")
    }

    private fun setErrorView(content: String) {
        tip.set(content)
        binding.tvTip?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.base_default_text_error
            )
        )
    }

    private fun setNormalViw(content: String) {
        tip.set(content)
        binding.tvTip?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.colorAccent
            )
        )
    }

    private fun onSure() {
//        if (userAttestationResponse != null) {
//            listener(userAttestationResponse ?: return)
//        }
//        dismiss()
    }

    private fun onClose() {
        dismissListener()
        dismiss()
    }

    override fun dismiss() {
        super.dismiss()
        if (irCameraHelper != null) {
            irCameraHelper?.release()
            irCameraHelper = null
        }
        if (rgbCameraHelper != null) {
            rgbCameraHelper?.release()
            rgbCameraHelper = null
        }
        liveModel?.destroy()
    }

    override fun onGlobalLayout() {
        binding.dualCameraTexturePreviewRgb.viewTreeObserver.removeOnGlobalLayoutListener(this)
        initRgbCamera()
    }

    private fun toHorizontalMirror(bmp: Bitmap): Bitmap {
        val w = bmp.width
        val h = bmp.height
        val matrix = Matrix()
        matrix.postScale(-1f, 1f) // 水平镜像翻转
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true)
    }

    private fun initRgbCamera() {
        val cameraListener: CameraListener = object : CameraListener {
            override fun onCameraOpened(
                camera: Camera,
                cameraId: Int,
                displayOrientation: Int,
                isMirror: Boolean
            ) {
                val previewSizeRgb = camera.parameters.previewSize
                val layoutParams: ViewGroup.LayoutParams = adjustPreviewViewSize(
                    binding.dualCameraTexturePreviewRgb,
                    binding.dualCameraTexturePreviewRgb, binding.dualCameraFaceRectView,
                    previewSizeRgb, displayOrientation, 1.0f
                )
                rgbFaceRectTransformer = FaceRectTransformer(
                    previewSizeRgb.width, previewSizeRgb.height,
                    layoutParams.width, layoutParams.height,
                    displayOrientation, cameraId, isMirror,
                    false,//todo ConfigUtil.isDrawRgbRectHorizontalMirror(LivenessDetectActivity.this)
                    false//todo ConfigUtil.isDrawRgbRectVerticalMirror(LivenessDetectActivity.this)
                )
                val textViewRgb = TextView(context, null)
                textViewRgb.layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
//                textViewRgb.setText(
//                    getString(
//                        R.string.camera_rgb_preview_size,
//                        previewSizeRgb.width,
//                        previewSizeRgb.height
//                    )
//                )
                textViewRgb.setTextColor(Color.WHITE)
                textViewRgb.setBackgroundColor(context.resources.getColor(R.color.color_bg_notification))
                (binding.dualCameraTexturePreviewRgb.parent as FrameLayout).addView(textViewRgb)
                liveModel?.onRgbCameraOpened(camera)
                liveModel?.setRgbFaceRectTransformer(rgbFaceRectTransformer)
            }

            override fun onPreview(nv21: ByteArray?, camera: Camera?) {
//                val base64FaceData = Base64.encodeToString(nv21, Base64.DEFAULT)
                binding.dualCameraFaceRectView.clearFaceInfo()
                binding.dualCameraFaceRectViewIr.clearFaceInfo()
                val facePreviewInfoList: List<FacePreviewInfo>? = liveModel?.onPreviewFrame(nv21)
                if (facePreviewInfoList != null && rgbFaceRectTransformer != null) {
                    drawPreviewInfo(facePreviewInfoList, nv21)
                }
            }

            override fun onCameraClosed() {
                Logcat.d(
                    KLog.TAG,
                    "onCameraClosed: "
                )
            }

            override fun onCameraError(e: Exception) {
                Logcat.e(
                    KLog.TAG,
                    "相机错误onCameraError: " + e.message
                )
                setErrorView("相机异常，请检查")
            }

            override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {
                if (rgbFaceRectTransformer != null) {
                    rgbFaceRectTransformer?.setCameraDisplayOrientation(displayOrientation)
                }
                Logcat.d(
                    KLog.TAG,
                    "onCameraConfigurationChanged: $cameraID  $displayOrientation"
                )
            }
        }
        rgbCameraHelper = DualCameraHelper.Builder()
            .previewViewSize(
                Point(
                    binding.dualCameraTexturePreviewRgb.measuredWidth,
                    binding.dualCameraTexturePreviewRgb.measuredHeight
                )
            )
            .rotation((context as Activity).windowManager.defaultDisplay.rotation)//竖屏强制设置成1
            .specificCameraId(previewConfig?.getRgbCameraId())
            .isMirror(false)
            .additionalRotation(0)
            .previewSize(liveModel?.loadPreviewSize())
            .previewOn(binding.dualCameraTexturePreviewRgb)
            .cameraListener(cameraListener)
            .build()
        Logcat.d(
            KLog.TAG,
            "defaultDisplay.rotation：" + (context as Activity).windowManager.defaultDisplay.rotation
        )
        rgbCameraHelper?.init()
        rgbCameraHelper?.start()
    }

    /**
     * 绘制RGB、IR画面的实时人脸信息
     *
     * @param facePreviewInfoList RGB画面的实时人脸信息
     */
    private fun drawPreviewInfo(facePreviewInfoList: List<FacePreviewInfo>, nv21: ByteArray?) {
        if (rgbFaceRectTransformer != null) {
            val rgbDrawInfoList: List<FaceRectView.DrawInfo>? =
                liveModel?.getDrawInfo(
                    facePreviewInfoList,
                    com.texeasy.utils.constants.LivenessType.RGB,
                    nv21
                )
            binding.dualCameraFaceRectView.drawRealtimeFaceInfo(rgbDrawInfoList)
        }
        if (irFaceRectTransformer != null) {
            val irDrawInfoList: List<FaceRectView.DrawInfo>? =
                liveModel?.getDrawInfo(
                    facePreviewInfoList,
                    com.texeasy.utils.constants.LivenessType.IR,
                    nv21
                )
            binding.dualCameraFaceRectViewIr.drawRealtimeFaceInfo(irDrawInfoList)
        }
    }

    /**
     * 调整View的宽高，使2个预览同时显示
     *
     * @param previewView        显示预览数据的view
     * @param faceRectView       画框的view
     * @param previewSize        预览大小
     * @param displayOrientation 相机旋转角度
     * @return 调整后的LayoutParams
     */
    private fun adjustPreviewViewSize(
        rgbPreview: View,
        previewView: View,
        faceRectView: FaceRectView,
        previewSize: Camera.Size,
        displayOrientation: Int,
        scale: Float
    ): ViewGroup.LayoutParams {
        val layoutParams = previewView.layoutParams
        val measuredWidth = previewView.measuredWidth
        val measuredHeight = previewView.measuredHeight
        var ratio = previewSize.height.toFloat() / previewSize.width.toFloat()
        if (ratio > 1) {
            ratio = 1 / ratio
        }
        if (displayOrientation % 180 == 0) {
            layoutParams.width = measuredWidth
            layoutParams.height = (measuredWidth * ratio).toInt()
        } else {
            layoutParams.height = measuredHeight
            layoutParams.width = (measuredHeight * ratio).toInt()
        }
        if (scale < 1f) {
            val rgbParam = rgbPreview.layoutParams
            layoutParams.width = (rgbParam.width * scale).toInt()
            layoutParams.height = (rgbParam.height * scale).toInt()
        } else {
            layoutParams.width *= scale.toInt()
            layoutParams.height *= scale.toInt()
        }
        val metrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(metrics)
        if (layoutParams.width >= metrics.widthPixels) {
            val viewRatio = layoutParams.width / metrics.widthPixels.toFloat()
            layoutParams.width /= viewRatio.toInt()
            layoutParams.height /= viewRatio.toInt()
        }
        if (layoutParams.height >= metrics.heightPixels) {
            val viewRatio = layoutParams.height / metrics.heightPixels.toFloat()
            layoutParams.width /= viewRatio.toInt()
            layoutParams.height /= viewRatio.toInt()
        }
        previewView.layoutParams = layoutParams
        faceRectView.setLayoutParams(layoutParams)
        return layoutParams
    }

    override fun onBackPressed() {
        //do nothing
    }
}