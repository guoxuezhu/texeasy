package com.texeasy.view.activity.demo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.common.base.BaseApplication
import com.example.common.binding.command.BindingCommand
import com.example.common.utils.KLog
import com.example.common.utils.SPUtils
import com.example.common.utils.ToastUtils
import com.texeasy.R
import com.texeasy.base.constant.StringConstant
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.base.widget.OneButtonDialog
import com.texeasy.base.widget.picker.PickerDialog
import com.texeasy.databinding.ActivityDemoBinding
import com.texeasy.hardware.hzdongcheng.HzHardwareManager
import com.texeasy.hardware.hzdongcheng.HzHardwareProvider
import com.texeasy.hardware.rfid.RFidManager
import com.texeasy.view.fragment.devicesetting.DeviceSettingModel
import com.uhf.uhf.serialport.SerialPortFinder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding
    private var initialComValue: String? = null
    val onReadCardCommand = BindingCommand<Any>(::onReadCard)
    val onScanCommand = BindingCommand<Any>(::onScan)
    val onBoxCommand = BindingCommand<Any>(::onBox)
    val onBoxStatusCommand = BindingCommand<Any>(::onBoxStatus)
    val onOpenFingerCommand = BindingCommand<Any>(::onOpenFinger)
    val onCloseFingerCommand = BindingCommand<Any>(::onCloseFinger)
    val onGetFingerFeatureCommand = BindingCommand<Any>(::onGetFingerFeature)
    val onMatchFingerFeatureCommand = BindingCommand<Any>(::onMatchFingerFeature)
    val onGetFingerTemplateCommand = BindingCommand<Any>(::onGetFingerTemplate)
    val onGetFingerImageCommand = BindingCommand<Any>(::onGetFingerImage)
    val onGetFingerVersionCommand = BindingCommand<Any>(::onGetFingerVersion)
    val onGetFingerVendorCommand = BindingCommand<Any>(::onGetFingerVendor)
    val onRfidCommand = BindingCommand<Any>(::onRfid)
    val onRfidStopCommand = BindingCommand<Any>(::onRfidStop)
    val onRfidPowerCommand = BindingCommand<Any>(::onRfidPower)
    val onComCommand = BindingCommand<Any>(::onCom)
    val onRfidLongCommand = BindingCommand<Any>(::onRfidLong)
    var boxCode = ObservableField("")
    var boxStatusCode = ObservableField("")
    var cardData = ObservableField("")
    var scanData = ObservableField("")
    var boxData = ObservableField("")
    var boxStatusData = ObservableField("")
    var rfidData = ObservableField("")
    var rfidReadCount = ObservableField("")
    var rfidReadLongCount = ObservableField(0)
    var antennasData = ObservableField("2")
    var rfidTime = ObservableField("3")
    var rfidCount = ObservableField("1")
    var rfidPower = ObservableField(28)
    var com = ObservableField("")
    var isReadCarding = false
    var isScaning = false
    var isInventorying = ObservableField(false)
    var isRfidDoing = ObservableField(false)
    var isRfidLongDoing = ObservableField(false)
    var pickerDialog: PickerDialog? = null
    var rfidLocalCount = 0
    var errorCount = ObservableField(0)
    var successCount = ObservableField(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.activity_demo,
            null,
            false
        )
        binding.viewModel = this
        setContentView(binding.root)
        com.set(
            SPUtils.getInstance().getString(
                StringConstant.COM_NAME,
                BaseApplication.getInstance().getString(R.string.base_com_ttymxc3)
            )
        )
        initialComValue = com.get()
    }

    private fun initData() {
        if (HzHardwareManager.init(this)) {
            ToastUtils.showShort("驱动服务连接成功")
        } else {
            ToastUtils.showShort("驱动服务连接失败")
        }
        RFidManager.connectRs232(com.get()!!)
        Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe {
            RFidManager.getWorkAntennaOutputPower(object : RFidManager.RfidSettingListener<Int> {
                override fun callBack(t: Int) {
                    rfidPower.set(t)
                }
            })
        }
    }

    private fun onReadCard() {
        if (isReadCarding) {
            ToastUtils.showShort("正在读卡中...")
            return
        }
        isReadCarding = true
        cardData.set("")
        HzHardwareProvider.getInstance().cardReaderController
        HzHardwareManager.setCardReadCallBack { data, type ->
            if (data.isNotEmpty()) {
                Logcat.d(KLog.TAG + "卡片数据", data)
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort(data)
                    }
                isReadCarding = false
                cardData.set(data)
                HzHardwareManager.stopReadCard()
            }
        }
        HzHardwareManager.startReadCard(object : PickerDialog.Action1<Boolean> {
            override fun call(t: Boolean) {
            }
        })
    }

    private fun onScan() {
        if (isScaning) {
            ToastUtils.showShort("正在扫码中...")
            return
        }
        isScaning = true
        scanData.set("")
        HzHardwareProvider.getInstance().scannerController
        HzHardwareManager.setScannerCallBack { data, type ->
            if (data.isNotEmpty()) {
                Logcat.d(KLog.TAG + "二维码数据", data)
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort(data)
                    }
                isScaning = false
                scanData.set(data)
                HzHardwareManager.stopScanning()
            }
        }
        HzHardwareManager.toggleBarcode(true)
        HzHardwareManager.toggleQRCode(true)
        HzHardwareManager.startScanning()
    }

    private fun onBox() {
        if (boxCode.get().isNullOrEmpty()) {
            ToastUtils.showShort("请输入门号")
        } else {
            if (HzHardwareManager.openBox(boxCode.get())) {
                boxData.set("${boxCode.get()} 号门已打开")
            } else {
                boxData.set("${boxCode.get()} 号门打开失败")
            }
        }
    }

    private fun onBoxStatus() {
        if (boxStatusCode.get().isNullOrEmpty()) {
            ToastUtils.showShort("请输入门号")
        } else {
            val boxStatus = HzHardwareManager.getBoxStatus(boxStatusCode.get())
            //箱门开关状态. 0:关闭 1:打开 9:未知
            when (boxStatus.openStatus) {
                0.toByte() -> {
                    boxStatusData.set("${boxStatusCode.get()} 号门已关闭")
                }
                1.toByte() -> {
                    boxStatusData.set("${boxStatusCode.get()} 号门已打开")
                }
                9.toByte() -> {
                    boxStatusData.set("${boxStatusCode.get()} 号门未知状态")
                }
            }

        }
    }

    private fun onOpenFinger() {
        val isOpen = HzHardwareManager.openFinger()
        ToastUtils.showShort(if (isOpen) "[HAL] 指纹打开成功 " else "[HAL] 指纹打开失败 ")
    }

    private fun onCloseFinger() {
        val isClose = HzHardwareManager.closeFinger()
        ToastUtils.showShort(if (isClose) "[HAL] 指纹关闭成功 " else "[HAL] 指纹关闭失败 ")
    }

    private fun onGetFingerFeature() {
        rfidData.set("")
        val result = HzHardwareManager.getFingerFeature()
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 获取指纹特征码成功，特征值为：" + result.data else "[HAL] 获取指纹特征码失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun onMatchFingerFeature() {
        val getResult = HzHardwareManager.getFingerFeature()
        val isGetSuccess: Boolean =
            getResult != null && getResult.code == 0 && !TextUtils.isEmpty(getResult.data)
        val feature2 = getResult.data
        ToastUtils.showShort(
            if (isGetSuccess) "[HAL] 获取指纹特征码成功，特征值为：" + getResult.data else "[HAL] 获取指纹特征码失败 "
        )
        val result = HzHardwareManager.matchFingerFeature(rfidData.get(), feature2, 0)
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 比对指纹特征码成功，特征值为：" + result.data else "[HAL] 比对指纹特征码失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun onGetFingerTemplate() {
        val result = HzHardwareManager.getFingerTemplate()
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 获取指纹模板成功，值为：" + result.data else "[HAL] 获取指纹模板失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
            val byte = Base64.decode(result.data, Base64.DEFAULT)
            binding.ivFingerImage.setImageBitmap(bytes2Bimap(byte))
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun onGetFingerImage() {
        val result = HzHardwareManager.onGetFingerImage()
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 获取指纹图像成功，值为：" + result.data else "[HAL] 获取指纹图像失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
            val byte = Base64.decode(result.data, Base64.DEFAULT)
            binding.ivFingerImage.setImageBitmap(bytes2Bimap(byte))
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun onGetFingerVersion() {
        val result = HzHardwareManager.onGetFingerVersion()
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 获取指纹仪版本成功，值为：" + result.data else "[HAL] 获取指纹仪版本失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun onGetFingerVendor() {
        val result = HzHardwareManager.onGetFingerVendor()
        val isSuccess: Boolean =
            result != null && result.code == 0 && !TextUtils.isEmpty(result.data)
        ToastUtils.showShort(
            if (isSuccess) "[HAL] 获取指纹仪厂家成功，值为：" + result.data else "[HAL] 获取指纹仪厂家失败 "
        )
        if (isSuccess) {
            rfidData.set(result.data)
        } else {
            rfidData.set(result.errorMsg)
        }
    }

    private fun bytes2Bimap(b: ByteArray): Bitmap? {
        return if (b.isNotEmpty()) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            null
        }
    }

    private fun onRfid() {
        if (isInventorying.get()!! || isRfidDoing.get()!!) {
            ToastUtils.showShort("正在盘点中，等待完成该次盘点任务...")
            return
        }
        errorCount.set(0)
        successCount.set(0)
        isRfidDoing.set(true)
        isInventorying.set(true)
        rfidData.set("")
        rfidReadCount.set("")
        if (antennasData.get().isNullOrEmpty()) {
            ToastUtils.showShort("请输入天线号，如果多个天线以\",\"分隔")
            isRfidDoing.set(false)
            isInventorying.set(false)
            rfidLocalCount = 0
            return
        }
        var split = antennasData.get()?.split(",")
        if (split.isNullOrEmpty()) {
            ToastUtils.showShort("请输入天线号，如果多个天线以\",\"分隔")
            isRfidDoing.set(false)
            isInventorying.set(false)
            rfidLocalCount = 0
            return
        }
        try {
            rfidTime.get()?.toLong()
            rfidCount.get()?.toLong()
        } catch (e: Exception) {
            ToastUtils.showShort("盘存时间和盘存次数为数字")
            isRfidDoing.set(false)
            isInventorying.set(false)
            rfidLocalCount = 0
            return
        }
        val antennas = split.map { AntennaInfo(it.toByte(), 20) }
        rfidRead(antennas)
    }

    private fun rfidRead(antennas: List<AntennaInfo>) {
        try {
            rfidTime.get()?.toLong()
            rfidCount.get()?.toLong()
        } catch (e: Exception) {
            Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    ToastUtils.showShort("盘存时间和盘存次数为数字")
                }
            isRfidDoing.set(false)
            isInventorying.set(false)
            rfidLocalCount = 0
            return
        }
        rfidData.set("")
        rfidReadCount.set("")
        RFidManager.startRead(antennas, object : RFidManager.RfidListener {
            override fun callBack(epcList: List<String>, isTimeOut: Boolean) {
                rfidReadCount.set("${epcList.size}")
                Logcat.d(KLog.TAG + "数量为${epcList.size}：$epcList")
                rfidData.set(epcList.toString())
                rfidLocalCount++
                isInventorying.set(false)
                if (epcList.isNullOrEmpty()) {
                    errorCount.set(errorCount.get()?.plus(1))
                } else {
                    successCount.set(successCount.get()?.plus(1))
                }
                if (rfidLocalCount >= rfidCount.get()?.toLong()!!) {
                    isRfidDoing.set(false)
                    rfidLocalCount = 0
                }
                if (isRfidDoing.get()!!) {
                    Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe {
                        rfidRead(antennas)
                    }
                }
            }

            override fun error(error: String) {
                isInventorying.set(false)
            }
        }, rfidTime.get()?.toLong()!! * 1000)
    }

    private fun onRfidLong() {
        if (isRfidLongDoing.get()!!) {
            ToastUtils.showShort("正在长盘点中...")
        }
        errorCount.set(0)
        successCount.set(0)
        isRfidLongDoing.set(true)
        rfidData.set("")
        rfidReadCount.set("")
        if (antennasData.get().isNullOrEmpty()) {
            ToastUtils.showShort("请输入天线号，如果多个天线以\",\"分隔")
            isRfidLongDoing.set(false)
            return
        }
        var split = antennasData.get()?.split(",")
        if (split.isNullOrEmpty()) {
            ToastUtils.showShort("请输入天线号，如果多个天线以\",\"分隔")
            isRfidLongDoing.set(false)
            return
        }
        val antennas = split.map { AntennaInfo(it.toByte(), 20) }
        RFidManager.startRead(antennas, object : RFidManager.RfidListener {
            override fun callBack(epcList: List<String>, isTimeOut: Boolean) {
                rfidReadCount.set("${epcList.size}")
                Logcat.d(KLog.TAG + "数量为${epcList.size}：$epcList")
                rfidData.set(epcList.toString())
                if (epcList.isNullOrEmpty()) {
                    errorCount.set(errorCount.get()?.plus(1))
                } else {
                    successCount.set(successCount.get()?.plus(1))
                }
            }

            override fun error(error: String) {
                isRfidLongDoing.set(false)
            }
        }, 365 * 24 * 60 * 60 * 1000L, isRealTime = true, isCheckRepeat = false)
    }

    private fun onRfidStop() {
        if (isRfidLongDoing.get()!!) {
            RFidManager.stopRead()
        }
        isRfidDoing.set(false)
        isRfidLongDoing.set(false)
        rfidLocalCount = 0
    }

    private fun onRfidPower() {
        try {
            val toBytePower = rfidPower.get()!!.toByte()
            if (toBytePower < 0 || toBytePower > 33) {
                ToastUtils.showShort("输入的功率必须是0-33之间")
                return
            }
        } catch (e: NumberFormatException) {
            ToastUtils.showShort("输入的功率必须是数字")
            return
        }
        RFidManager.setWorkAntennaOutputPower(
            mutableListOf(
                AntennaInfo(
                    1,
                    rfidPower.get()!!.toByte()
                )
            )
        )
    }

    private fun onCom() {
        var comList = SerialPortFinder().allDevicesPath.toList()
        if (comList.isNullOrEmpty()) {
            comList = DeviceSettingModel().getDemoComList(BaseApplication.getInstance())
        }
        pickerDialog = PickerDialog(comList, 4,
            object : PickerDialog.Action1<Int> {
                override fun call(t: Int) {
                    val positionToCom = comList[t]
                    com.set(positionToCom)
                    SPUtils.getInstance().put(StringConstant.COM_NAME, positionToCom)
                    pickerDialog?.dismiss()
                    if (!com.get().equals(initialComValue)) {
                        OneButtonDialog(this@DemoActivity)
                            .setDetailText(R.string.base_com_modify_tip)
                            .setButtonText(R.string.base_com_restart_tip)
                            .setAllowBackPress(false)
                            .setListener { getRestartIntent(this@DemoActivity) }
                            .show()
                    }
                }
            })
        pickerDialog?.show(this.supportFragmentManager, pickerDialog?.tag)
    }

    private fun getRestartIntent(context: Context) {
        val killIntent = Intent(context, DemoActivity::class.java)
        killIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(killIntent)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    override fun onDestroy() {
        HzHardwareManager.release()
        RFidManager.release()
        super.onDestroy()
    }
}