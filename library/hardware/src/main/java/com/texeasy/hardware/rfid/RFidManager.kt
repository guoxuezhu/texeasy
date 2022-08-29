package com.texeasy.hardware.rfid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.common.base.BaseApplication
import com.example.common.utils.KLog
import com.example.common.utils.ToastUtils
import com.lazy.library.logging.Logcat
import com.texeasy.base.constant.ConfigCenter
import com.texeasy.base.entity.AntennaInfo
import com.texeasy.hardware.R
import com.texeasy.hardware.rfid.reader.base.CMD
import com.texeasy.hardware.rfid.reader.base.ERROR
import com.texeasy.hardware.rfid.reader.base.ReaderBase
import com.texeasy.hardware.rfid.reader.helper.*
import com.uhf.uhf.serialport.SerialPort
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.io.IOException
import java.security.InvalidParameterException
import java.util.*
import kotlin.experimental.and

class RFidManager {
    companion object {
        private val TAG = "RFidManager"
        private var DEBUG = ConfigCenter.newInstance().isDebug
        private var mSerialPort: SerialPort? = null
        private var mReaderHelper: ReaderHelper? = null
        private var mReader: ReaderBase? = null
        private var m_curReaderSetting: ReaderSetting? = null
        private var m_curInventoryBuffer: InventoryBuffer? = null
        private var m_curOperateTagBuffer: OperateTagBuffer? = null
        private var m_curOperateTagISO18000Buffer: ISO180006BOperateTagBuffer? = null
        private var lbm: LocalBroadcastManager? = null
        private var listener: RfidListener? = null
        private var timer: Timer? = null
        private var isRealTime: Boolean = false
        private var isStop: Boolean = false
        private var isCheckRepeat: Boolean = true
        private var btAntStatusArray = byteArrayOf()
        private var lastKeySize = 0
        private var sameNumber = 0
        private const val MAX_SAME_NUMBER = 2
        private var nCurrentAntId = -1
        private var isHasRecordCurrentAntId = false
        private var settingListener: RfidSettingListener<Int>? = null

        fun connectRs232(com: String) {
            try {
                mSerialPort =
                    SerialPort(
                        File(com),
                        115200,
                        0
                    ) //ly///dev/ttyMT1///dev/ttyS0
                try {
                    ReaderHelper.setContext(BaseApplication.getInstance())
                    mReaderHelper = ReaderHelper.getDefaultHelper()
                    mReaderHelper?.apply {
                        setReader(mSerialPort?.inputStream, mSerialPort?.outputStream)
                        mReader = reader
                        m_curReaderSetting = curReaderSetting
                        m_curInventoryBuffer = curInventoryBuffer
                        m_curOperateTagBuffer = curOperateTagBuffer
                        m_curOperateTagISO18000Buffer = curOperateTagISO18000Buffer
                    }
                    // 注册
                    registerReceiver()
                    initData()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            } catch (e: SecurityException) {
                ToastUtils.showShort(
                    BaseApplication.getInstance().resources.getString(R.string.error_security)
                )
            } catch (e: IOException) {
                ToastUtils.showShort(
                    BaseApplication.getInstance().resources.getString(R.string.error_unknown)
                )
            } catch (e: InvalidParameterException) {
                ToastUtils.showShort(
                    BaseApplication.getInstance().resources.getString(R.string.error_configuration)
                )
            }
        }

        private fun openAnt(antennas: List<Byte>) {
            // 开启天线1、2、3、4
            m_curInventoryBuffer?.apply {
                lAntenna.clear()
                lAntenna.addAll(antennas)
            }
        }

        private fun registerReceiver() {
            lbm = LocalBroadcastManager.getInstance(BaseApplication.getInstance())
            val intent = IntentFilter()
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_FAST_SWITCH)
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY)
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL)
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_ISO18000_6B)
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG)
            intent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING)
            intent.addAction(ReaderHelper.BROADCAST_WRITE_LOG)
            intent.addAction(ReaderHelper.BROADCAST_WRITE_DATA)
            lbm?.registerReceiver(mRec, intent)
        }

        private fun initData() {
            getAntPhysicalConnectionStatus()
        }

        /**
         * 开始读芯片、关闭读芯片
         */
        fun startRead(
            antennas: List<AntennaInfo>,
            listener: RfidListener,
            scanTime: Long = (ConfigCenter.newInstance().rfidScanTime.get()!! * 1000).toLong(),
            isRealTime: Boolean = false,//是否实时监听返回
            isCheckRepeat: Boolean = true//是否检测重复性
        ) {
            Logcat.e(KLog.TAG, "当前扫描的天线：$antennas")
            val resultAntennas = antennas.distinctBy { it.antenna }
                .map { AntennaInfo((it.antenna - 1).toByte(), it.power) }
            if (!checkAntStatus(resultAntennas)) {
                listener.error("盘点中存在没有连接的天线")
                return
            }
            resetStatus()
            clearInventoryRealResult()
            isStop = false
            RFidManager.isRealTime = isRealTime
            RFidManager.isCheckRepeat = isCheckRepeat
            // 开启4个天线
            openAnt(resultAntennas.map { it.antenna })
            this.listener = listener
            Logcat.e(KLog.TAG + "开启天线扫描功能", Date().toString())
            //m_curInventoryBuffer.clearInventoryPar();
            m_curInventoryBuffer?.apply {
                nIndexAntenna = 0
                nCommond = 0
                bLoopInventoryReal = true
                btRepeat = 1
            }
            if ((m_curInventoryBuffer?.btRepeat!! and 0xFF.toByte()) <= 0) {
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort(
                            BaseApplication.getInstance().resources.getString(R.string.repeat_min)
                        )
                    }
                return
            }
            m_curInventoryBuffer?.bLoopCustomizedSession = false
            if (m_curInventoryBuffer?.lAntenna?.size!! <= 0) {
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort(
                            BaseApplication.getInstance().resources.getString(R.string.antenna_empty)
                        )
                    }
                return
            }
            mReaderHelper?.inventoryFlag = true
            mReaderHelper?.clearInventoryTotal()
            var btWorkAntenna: Byte = 0
            if (m_curInventoryBuffer?.lAntenna?.size!! > 0) {
                btWorkAntenna =
                    m_curInventoryBuffer?.lAntenna?.get(m_curInventoryBuffer?.nIndexAntenna!!)!!
                if (btWorkAntenna < 0) btWorkAntenna = 0
            }
            m_curReaderSetting?.btWorkAntenna = btWorkAntenna
            mReaderHelper?.runLoopInventroy()

            // 开始扫描
            timer = Timer()
            timer?.schedule(getTimerTask(), scanTime * resultAntennas.size)
        }

        //用于app释放
        fun release() {
            stop()
            lbm?.unregisterReceiver(mRec)
        }

        fun stopRead() {
            stop()
            m_curInventoryBuffer?.apply {
                //停止循环实时盘存：runLoopInventroy
                bLoopInventoryReal = false
                val epcList = dtIndexMap.keys.filterNot { it.isNullOrEmpty() }
                if (epcList.isNotEmpty()) {
                    val noBlankEpcList = epcList.map { it.trim().replace(" ", "") }
                    listener?.callBack(noBlankEpcList)
                } else {
                    listener?.callBack(epcList)
                }
                clearInventoryRealResult()
            }
        }

        private fun stop() {
            Logcat.e(KLog.TAG + "关闭天线扫描功能", Date().toString())
            resetStatus()

            // 取消定时任务
            timer?.cancel()
            timer = null
        }

        private fun resetStatus() {
            isRealTime = false
            isStop = true
            isCheckRepeat = true
            isHasRecordCurrentAntId = false
            nCurrentAntId = -1
            sameNumber = 0
            lastKeySize = 0
            //停止循环（用在设置工作天线：processSetWorkAntenna）
            mReaderHelper?.inventoryFlag = false
        }

        private fun getTimerTask(): TimerTask? {
            return object : TimerTask() {
                override fun run() {
                    stopRead()
                }
            }
        }

        private val mRec: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (isStop) {
                    return
                }
                if (intent.action == ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL) {
                    val btCmd = intent.getByteExtra("cmd", 0x00.toByte())
                    when (btCmd) {
                        ReaderHelper.INVENTORY_END -> {
                            m_curInventoryBuffer?.apply {
                                if (dtIndexMap.keys.size > 0) {
                                    val epcList = dtIndexMap.keys.filterNot { it.isNullOrEmpty() }
                                    if (isCheckRepeat) {
                                        val currentKeySize = epcList.size
                                        if (lastKeySize != currentKeySize) {
                                            lastKeySize = currentKeySize
                                        } else {
                                            //统计同一个天线号盘点到相同数据次数
                                            if (nCurrentAntId == nCurrentAnt) {
                                                sameNumber++
                                            }
                                        }
                                        //相同次数大于最大相同次数
                                        if (sameNumber >= MAX_SAME_NUMBER) {
                                            stopRead()
                                            return
                                        }
                                        if (!isHasRecordCurrentAntId) {
                                            nCurrentAntId = nCurrentAnt
                                            isHasRecordCurrentAntId = true
                                        }
                                    }
                                    if (isRealTime) {
                                        if (epcList.isNotEmpty()) {
                                            val noBlankEpcList =
                                                epcList.map { it.trim().replace(" ", "") }
                                            listener?.callBack(noBlankEpcList, false)
                                        } else {
                                            listener?.callBack(epcList, false)
                                        }
                                    }
                                    //Log.d(TAG, epcList.toString())
                                }
                            }
                        }
                    }
                } else if (intent.action == ReaderHelper.BROADCAST_REFRESH_READER_SETTING) {
                    val btCmd = intent.getByteExtra("cmd", 0x00.toByte())
                    when (btCmd) {
                        CMD.GET_ANT_PHYSICAL_CONNECTION_STATUS -> {
                            btAntStatusArray = m_curReaderSetting?.btAntStatus!!
                            Logcat.d(KLog.TAG, "天线状态：${btAntStatusArray.toList().toString()}")
                        }
                        CMD.SET_OUTPUT_POWER -> {
                            mReader?.getOutputPower(m_curReaderSetting?.btReadId!!)
                        }
                        CMD.GET_OUTPUT_POWER -> {
                            m_curReaderSetting?.btAryOutputPower?.get(0)?.toInt()?.let {
                                settingListener?.callBack(it)
                                settingListener = null
                            }
                            Observable.just(true)
                                .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                                    ToastUtils.showShort(
                                        "当前天线功率为：${
                                            m_curReaderSetting?.btAryOutputPower?.get(
                                                0
                                            )
                                        }"
                                    )
                                }
                        }
                    }
                } else if (intent.action == ReaderHelper.BROADCAST_WRITE_LOG) {
                    val type = intent.getIntExtra("type", ERROR.SUCCESS.toInt())
                    val log = intent.getStringExtra("log")
                    if (type != ERROR.SUCCESS.toInt()) {
                        Logcat.e(KLog.TAG, log)
                    }
                }
            }
        }

        /**
         * 清除实时盘点数据
         */
        fun clearInventoryRealResult() {
            m_curInventoryBuffer?.clearInventoryRealResult()
        }

        /**
         * 设置天线输出功率
         */
        fun setWorkAntennaOutputPower(antennas: List<AntennaInfo>) {
            val resultAntennas = antennas.map { AntennaInfo((it.antenna - 1).toByte(), it.power) }
            val list = resultAntennas.toMutableList().distinctBy { it.antenna }
            if (list.isEmpty()) {
                return
            }
            mReader?.setOutputPower(
                m_curReaderSetting?.btReadId!!,
                list[0].power,
                list[0].power,
                list[0].power,
                list[0].power
            )
        }

        fun getWorkAntennaOutputPower(listener: RfidSettingListener<Int>) {
            mReader?.getOutputPower(m_curReaderSetting?.btReadId!!)
            settingListener = listener
        }

        /**
         * 检查天线连接状态
         */
        private fun checkAntStatus(antennas: List<AntennaInfo>): Boolean {
            var result = true
            var errorAnts = mutableListOf<Byte>()
            var antNos = antennas.map { it.antenna }.distinct()
            if (btAntStatusArray.isEmpty()) {
                //重新获取天线状态
                getAntPhysicalConnectionStatus()
                Logcat.d(KLog.TAG, "天线状态数组为空")
                return false
            }
            for (antNo in antNos) {
                if (antNo.toInt() < 0) {
                    Logcat.d(KLog.TAG, "天线号不能小于1：${antNo + 1}")
                    return false
                }
                if (btAntStatusArray[antNo.toInt()] == 0.toByte()) {
                    result = false
                    errorAnts.add((antNo + 1).toByte())
                }
            }
            if (errorAnts.isNotEmpty()) {
                Logcat.d(KLog.TAG, "天线连接失败的号码为：$errorAnts")
                Observable.just(true)
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                        ToastUtils.showShort("天线连接失败的号码为：$errorAnts")
                    }
            }
            return result
        }

        /**
         * 获取天线连接状态
         */
        private fun getAntPhysicalConnectionStatus() {
            mReader?.getAntPhysicalConnectionStatus(m_curReaderSetting?.btReadId!!)
        }
    }

    interface RfidListener {
        /**
         * @param isTimeOut 是否是超时返回
         */
        fun callBack(epcList: List<String>, isTimeOut: Boolean = true) {}

        fun error(error: String) {}
    }

    interface RfidSettingListener<T> {
        fun callBack(t: T)
    }
}