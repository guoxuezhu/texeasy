package com.texeasy.hardware.rfid.reader.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.common.utils.KLog;
import com.lazy.library.logging.Logcat;
import com.texeasy.hardware.R;
import com.texeasy.hardware.rfid.reader.base.CMD;
import com.texeasy.hardware.rfid.reader.base.Converter;
import com.texeasy.hardware.rfid.reader.base.ERROR;
import com.texeasy.hardware.rfid.reader.base.HEAD;
import com.texeasy.hardware.rfid.reader.base.MessageTran;
import com.texeasy.hardware.rfid.reader.base.ReaderBase;
import com.texeasy.hardware.rfid.reader.base.StringTool;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

public class ReaderHelper {
    /**
     * The mark indicating that serialport can't read, it send broadcast action "BROADCAST_ON_LOST_CONNECT"
     */
    public final static String BROADCAST_ON_LOST_CONNECT = "com.reader.helper.onLostConnect";
    /**
     * Mark for writing tag, it send broadcast"actionΪBROADCAST_WRITE_DATA" after it written successfully
     */
    public final static String BROADCAST_WRITE_DATA = "com.reader.helper.writeData";
    /**
     * Mark to Read/Write log, it send broadcast action "BROADCAST_WRITE_LOG" after it's read/written successfully
     */
    public final static String BROADCAST_WRITE_LOG = "com.reader.helper.writeLog";
    /**
     * Mark for reader set specification, it send broadcast action "BROADCAST_REFRESH_READER_SETTING" after new setted specification is read successfully
     */
    public final static String BROADCAST_REFRESH_READER_SETTING = "com.reader.helper.refresh.readerSetting";
    /**
     * Mark for reader to inventory, it sends "BROADCAST_REFRESH_INVENTORY" after tag is inventoryed successfully one time
     */
    public final static String BROADCAST_REFRESH_INVENTORY = "com.reader.helper.refresh.inventory";
    /**
     * Mark for reader inventory real time, it send broadcast "BROADCAST_REFRESH_INVENTORY_REAL" after any one tag is inventoryed successfully
     */
    public final static String BROADCAST_REFRESH_INVENTORY_REAL = "com.reader.helper.refresh.inventoryReal";
    /**
     * Mark for antenna fast switch, it send broadcast"BROADCAST_REFRESH_FAST_SWITCH" after antenna is switched successfully
     */
    public final static String BROADCAST_REFRESH_FAST_SWITCH = "com.reader.helper.refresh.fastSwitch";
    /**
     * Mark for operating tag, it send broadcast "BROADCAST_REFRESH_OPERATE_TAG" when the tag is operated successfully
     */
    public final static String BROADCAST_REFRESH_OPERATE_TAG = "com.reader.helper.refresh.operateTag";
    /**
     * Mark for operating 6B tag, it send broadcast "BROADCAST_REFRESH_ISO18000_6B" after the 6B tag is operated successfully
     */
    public final static String BROADCAST_REFRESH_ISO18000_6B = "com.reader.helper.refresh.ISO180006B";

    private static LocalBroadcastManager mLocalBroadcastManager = null;

    /**
     * Error code for wrong inventory
     */
    public final static byte INVENTORY_ERR = 0x00;
    /**
     * End code for wrong inventory
     */
    public final static byte INVENTORY_ERR_END = 0x01;
    /**
     * End code for inventory
     */
    public final static byte INVENTORY_END = 0x02;

    @Deprecated
    public final static int WRITE_LOG = 0x10;
    @Deprecated
    public final static int REFRESH_READER_SETTING = 0x11;
    @Deprecated
    public final static int REFRESH_INVENTORY = 0x12;
    @Deprecated
    public final static int REFRESH_INVENTORY_REAL = 0x13;
    @Deprecated
    public final static int REFRESH_FAST_SWITCH = 0x14;
    @Deprecated
    public final static int REFRESH_OPERATE_TAG = 0x15;
    @Deprecated
    public final static int REFRESH_ISO18000_6B = 0x15;

    @Deprecated
    public final static int LOST_CONNECT = 0x20;

    private static ReaderBase mReader;
    private static Context mContext;

    private static ReaderHelper mReaderHelper;

    private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    private static ISO180006BOperateTagBuffer m_curOperateTagISO18000Buffer;

    private boolean m_bInventory = false;
    private boolean m_bISO6BContinue = false;

    private int m_nTotal = 0;

//    private MainActivity mMcontex;//ly

    /**
     * Constructor
     */
    public ReaderHelper() {
        m_curReaderSetting = new ReaderSetting();
        m_curInventoryBuffer = new InventoryBuffer();
        m_curOperateTagBuffer = new OperateTagBuffer();
        m_curOperateTagISO18000Buffer = new ISO180006BOperateTagBuffer();
    }

    /**
     * Set Context.
     *
     * @param context Set Context
     * @throws Exception Throw an error when the Context is empty
     */
    public static void setContext(Context context) throws Exception {
        mContext = context;
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);

        mReaderHelper = new ReaderHelper();
    }

    /**
     * Returns the global reader helper class in helper.
     *
     * @return Returns the global reader helper class in helper
     * @throws Exception Throw an error when the global reader helper class is empty
     */
    public static ReaderHelper getDefaultHelper() throws Exception {

        if (mReaderHelper == null || mContext == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");

        return mReaderHelper;
    }

    /**
     * Set cycle flag.
     *
     * @param flag flag
     */
    public void setInventoryFlag(boolean flag) {
        this.m_bInventory = flag;
    }

    /**
     * Get cycle flag.
     *
     * @return flag
     */
    public boolean getInventoryFlag() {
        return this.m_bInventory;
    }

    /**
     * Set cycle flag.
     *
     * @param flag flag
     */
    public void setISO6BContinue(boolean m_continue) {
        this.m_bISO6BContinue = m_continue;
    }

    /**
     * Get cycle flag.
     *
     * @return flag
     */
    public boolean getISO6BContinue() {
        return this.m_bISO6BContinue;
    }

    public int getInventoryTotal() {
        return this.m_nTotal;
    }

    public void setInventoryTotal(int num) {
        this.m_nTotal = num;
    }

    public void clearInventoryTotal() {
        this.m_nTotal = 0;
    }

    public ReaderSetting getCurReaderSetting() {
        return m_curReaderSetting;
    }

    public InventoryBuffer getCurInventoryBuffer() {
        return m_curInventoryBuffer;
    }

    public OperateTagBuffer getCurOperateTagBuffer() {
        return m_curOperateTagBuffer;
    }

    public ISO180006BOperateTagBuffer getCurOperateTagISO18000Buffer() {
        return m_curOperateTagISO18000Buffer;
    }

    /**
     * Display log.
     *
     * @param strLog log information
     * @param type   log grade(0x10:Right, 0x11:Wrong)
     */
    private void writeLog(String strLog, int type) {
        Intent itent = new Intent(BROADCAST_WRITE_LOG);
        itent.putExtra("type", type);
        itent.putExtra("log", strLog);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * Refresh to display parameter of reader.
     *
     * @param btCmd      Command Type(Refresh specify type)
     * @param curSetting Current reader parameter
     */
    private void refreshReaderSetting(byte btCmd, ReaderSetting curReaderSetting) {
        Intent itent = new Intent(BROADCAST_REFRESH_READER_SETTING);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * Inventory(Buffer Mode)，Tag data refresh.
     *
     * @param btCmd              Command Type(Refresh specify type)
     * @param curInventoryBuffer Current tag data
     */
    private void refreshInventory(byte btCmd, InventoryBuffer curInventoryBuffer) {
        Intent itent = new Intent(BROADCAST_REFRESH_INVENTORY);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * Inventory(Real Time Mode)，Tag data refresh.
     *
     * @param btCmd              Command Type(Refresh specify type)
     * @param curInventoryBuffer Current tag data
     */
    private void refreshInventoryReal(byte btCmd,
                                      InventoryBuffer curInventoryBuffer) {
        Intent itent = new Intent(BROADCAST_REFRESH_INVENTORY_REAL);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * Inventory(Fast Switch Antenna Mode)，Tag data refresh.
     *
     * @param btCmd              Command Type(Refresh specify type)
     * @param curInventoryBuffer Current tag data
     */
    private void refreshFastSwitch(byte btCmd,
                                   InventoryBuffer curInventoryBuffer) {
        Intent itent = new Intent(BROADCAST_REFRESH_FAST_SWITCH);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * Inventory(Fast Switch Antenna Mode)，Tag data refresh.
     *
     * @param btCmd               Command Type(Refresh specify type)
     * @param curOperateTagBuffer Current tag data
     */
    private void refreshOperateTag(byte btCmd,
                                   OperateTagBuffer curOperateTagBuffer) {
        Intent itent = new Intent(BROADCAST_REFRESH_OPERATE_TAG);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    }

    ;

    /**
     * ISO18000-6B Tag data refresh.
     *
     * @param btCmd                       Command Type(Refresh specify type)
     * @param curOperateTagISO18000Buffer Current tag data
     */
    private void refreshISO180006B(byte btCmd,
                                   ISO180006BOperateTagBuffer curOperateTagISO18000Buffer) {
        Intent itent = new Intent(BROADCAST_REFRESH_ISO18000_6B);
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);

        if (m_bISO6BContinue) {
            if (btCmd == INVENTORY_END || btCmd == INVENTORY_ERR_END)
                mReader.iso180006BInventory(m_curReaderSetting.btReadId);
        }
    }

    ;

    /**
     * Set and return the global reader helper base class in helper.
     *
     * @param in  Input stream
     * @param out Output stream
     * @return Global reader helper base class in helper
     * @throws Exception Throw an error when in or out is empty
     */
    public ReaderBase setReader(InputStream in, OutputStream out)
            throws Exception {

        if (in == null || out == null)
            throw new NullPointerException("in Or out is NULL!");

        if (mReader == null) {

            mReader = new ReaderBase(in, out) {

                boolean flag = false;
                int length = 0;
                byte[] dataBuffer = new byte[50];

                int sendLength = 0;
                byte[] sendDataBuffer = new byte[50];

                @Override
                public void onLostConnect() {
                    mLocalBroadcastManager.sendBroadcast(new Intent(
                            BROADCAST_ON_LOST_CONNECT));
                }

                @Override
                public void analyData(MessageTran msgTran) {
                    mReaderHelper.analyData(msgTran);
                }

                @Override
                public void reciveData(byte[] btAryReceiveData) {
                    /*System.arraycopy(btAryReceiveData, 0, dataBuffer,length, btAryReceiveData.length);
                    length = length + btAryReceiveData.length;
					if (length >= 6) {*/
                    String strLog = StringTool.byteArrayToString(
                            btAryReceiveData, 0, btAryReceiveData.length);
                    Intent itent = new Intent(BROADCAST_WRITE_DATA);
                    itent.putExtra("type", ERROR.SUCCESS & 0xFF);
                    itent.putExtra("log", strLog);
                    mLocalBroadcastManager.sendBroadcast(itent);
                        /*length = 0;
						flag = true;
					}*/
                }

                @Override
                public void sendData(byte[] btArySendData) {
					/*System.arraycopy(btArySendData, 0, sendDataBuffer,sendLength, btArySendData.length);
					sendLength = sendLength + btArySendData.length;
					if (sendLength >= 5) {*/
                    String strLog = StringTool.byteArrayToString(btArySendData,
                            0, btArySendData.length);
                    Intent itent = new Intent(BROADCAST_WRITE_DATA);
                    itent.putExtra("type", ERROR.FAIL & 0xFF);
                    itent.putExtra("log", strLog);
                    mLocalBroadcastManager.sendBroadcast(itent);
					/*	sendLength = 0;
					}*/
                }

                @Override
                public void refreshBuffer() {
                    length = 0;
                    sendLength = 0;
                }
            };
        }

        return mReader;
    }

    /**
     * Return the global reader helper base class in helper.
     *
     * @return Global reader helper base class in helper
     * @throws Exception Throw an error when the global reader helper class is empty
     */
    public ReaderBase getReader() throws Exception {
        if (mReader == null) {
            throw new NullPointerException("mReader is Null!");
        }

        return mReader;
    }

    /**
     * Rewritable function, can call this function after resolution to a packet of data.
     *
     * @param msgTran parsed package
     */
    private void analyData(MessageTran msgTran) {
        if (msgTran.getPacketType() != HEAD.HEAD) {
            return;
        }

        switch (msgTran.getCmd()) {
            case CMD.GET_ANT_PHYSICAL_CONNECTION_STATUS:
                processGetAntPhysicalConnectionStatus(msgTran);
                break;
            case CMD.SET_WORK_MODE:
                processSetWorkMode(msgTran);
                break;
            case CMD.GET_WORK_MODE:
                processGetWorkMode(msgTran);
                break;
            case CMD.RESET:
                processReset(msgTran);
                break;
            case CMD.SET_UART_BAUDRATE:
                processSetUartBaudrate(msgTran);
                break;
            case CMD.GET_FIRMWARE_VERSION:
                processGetFirmwareVersion(msgTran);
                break;
            case CMD.SET_READER_ADDRESS:
                processSetReaderAddress(msgTran);
                break;
            case CMD.SET_WORK_ANTENNA:
                processSetWorkAntenna(msgTran);
                break;
            case CMD.GET_WORK_ANTENNA:
                processGetWorkAntenna(msgTran);
                break;
            case CMD.SET_OUTPUT_POWER:
                processSetOutputPower(msgTran);
                break;
            case CMD.GET_OUTPUT_POWER:
                processGetOutputPower(msgTran);
                break;
            case CMD.SET_FREQUENCY_REGION:
                processSetFrequencyRegion(msgTran);
                break;
            case CMD.GET_FREQUENCY_REGION:
                processGetFrequencyRegion(msgTran);
                break;
            case CMD.SET_BEEPER_MODE:
                processSetBeeperMode(msgTran);
                break;
            case CMD.GET_READER_TEMPERATURE:
                processGetReaderTemperature(msgTran);
                break;
            case CMD.READ_GPIO_VALUE:
                processReadGpioValue(msgTran);
                break;
            case CMD.WRITE_GPIO_VALUE:
                processWriteGpioValue(msgTran);
                break;
            case CMD.SET_ANT_CONNECTION_DETECTOR:
                processSetAntConnectionDetector(msgTran);
                break;
            case CMD.GET_ANT_CONNECTION_DETECTOR:
                processGetAntConnectionDetector(msgTran);
                break;
            case CMD.SET_TEMPORARY_OUTPUT_POWER:
                processSetTemporaryOutputPower(msgTran);
                break;
            case CMD.SET_READER_IDENTIFIER:
                processSetReaderIdentifier(msgTran);
                break;
            case CMD.GET_READER_IDENTIFIER:
                processGetReaderIdentifier(msgTran);
                break;
            case CMD.SET_RF_LINK_PROFILE:
                processSetRfLinkProfile(msgTran);
                break;
            case CMD.GET_RF_LINK_PROFILE:
                processGetRfLinkProfile(msgTran);
                break;
            case CMD.GET_RF_PORT_RETURN_LOSS:
                processGetRfPortReturnLoss(msgTran);
                break;
            case CMD.INVENTORY:
                processInventory(msgTran);
                break;
            case CMD.READ_TAG:
                processReadTag(msgTran);
                break;
            case CMD.WRITE_TAG:
                processWriteTag(msgTran);
                break;
            case CMD.LOCK_TAG:
                processLockTag(msgTran);
                break;
            case CMD.KILL_TAG:
                processKillTag(msgTran);
                break;
            case CMD.SET_ACCESS_EPC_MATCH:
                processSetAccessEpcMatch(msgTran);
                break;
            case CMD.GET_ACCESS_EPC_MATCH:
                processGetAccessEpcMatch(msgTran);
                break;
            case CMD.REAL_TIME_INVENTORY:
                processRealTimeInventory(msgTran);
                break;
            case CMD.FAST_SWITCH_ANT_INVENTORY:
                processFastSwitchInventory(msgTran);
                break;
            case CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY:
                processCustomizedSessionTargetInventory(msgTran);
                break;
            case CMD.SET_IMPINJ_FAST_TID:
                processSetImpinjFastTid(msgTran);
                break;
            case CMD.SET_AND_SAVE_IMPINJ_FAST_TID:
                processSetAndSaveImpinjFastTid(msgTran);
                break;
            case CMD.GET_IMPINJ_FAST_TID:
                processGetImpinjFastTid(msgTran);
                break;
            case CMD.ISO18000_6B_INVENTORY:
                processISO180006BInventory(msgTran);
                break;
            case CMD.ISO18000_6B_READ_TAG:
                processISO180006BReadTag(msgTran);
                break;
            case CMD.ISO18000_6B_WRITE_TAG:
                processISO180006BWriteTag(msgTran);
                break;
            case CMD.ISO18000_6B_LOCK_TAG:
                processISO180006BLockTag(msgTran);
                break;
            case CMD.ISO18000_6B_QUERY_LOCK_TAG:
                processISO180006BQueryLockTag(msgTran);
                break;
            case CMD.GET_INVENTORY_BUFFER:
                processGetInventoryBuffer(msgTran);
                break;
            case CMD.GET_AND_RESET_INVENTORY_BUFFER:
                processGetAndResetInventoryBuffer(msgTran);
                break;
            case CMD.GET_INVENTORY_BUFFER_TAG_COUNT:
                processGetInventoryBufferTagCount(msgTran);
                break;
            case CMD.RESET_INVENTORY_BUFFER:
                processResetInventoryBuffer(msgTran);
                break;
            case CMD.OPERATE_TAG_MASK:
                processTagMask(msgTran);
                break;
            default:
                break;
        }
    }

    /**
     * Parse all feedback of set command.
     *
     * @param msgTran
     */
    private void processSet(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == ERROR.SUCCESS) {
                m_curReaderSetting.btReadId = msgTran.getReadId();

                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processReset(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processSetUartBaudrate(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetFirmwareVersion(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x02) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btMajor = btAryData[0];
            m_curReaderSetting.btMinor = btAryData[1];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetReaderAddress(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processSetWorkAntenna(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strErrorCode = "";
        String strCmd = mContext.getResources().getString(
                R.string.set_ant_success) + (m_curReaderSetting.btWorkAntenna + 1);

        if (btAryData.length == 0x01) {
            if (btAryData[0] == ERROR.SUCCESS) {
                m_curReaderSetting.btReadId = msgTran.getReadId();
                writeLog(strCmd, ERROR.SUCCESS);
                if (m_bInventory) {
                    runLoopInventroy();
                }
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd + mContext.getResources().getString(R.string.failure_reason) + strErrorCode;
        writeLog(strLog, ERROR.FAIL);

        if (m_bInventory) {
            m_curInventoryBuffer.nCommond = 1;
            m_curInventoryBuffer.dtEndInventory = new Date();
            runLoopInventroy();
        }
    }

    private void processGetWorkAntenna(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == 0x00 || btAryData[0] == 0x01
                    || btAryData[0] == 0x02 || btAryData[0] == 0x03) {
                m_curReaderSetting.btReadId = msgTran.getReadId();
                m_curReaderSetting.btWorkAntenna = btAryData[0];

                refreshReaderSetting(btCmd, m_curReaderSetting);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetOutputPower(MessageTran msgTran) {
        processSet(msgTran);
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        if (btAryData.length == 0x01) {
            if (btAryData[0] == ERROR.SUCCESS) {
                refreshReaderSetting(btCmd, m_curReaderSetting);
            }
        }
    }

    private void processGetOutputPower(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x04 || btAryData.length == 0x01) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btAryOutputPower = btAryData.clone();

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetFrequencyRegion(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetFrequencyRegion(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x03) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btRegion = btAryData[0];
            m_curReaderSetting.btFrequencyStart = btAryData[1];
            m_curReaderSetting.btFrequencyEnd = btAryData[2];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x06) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btRegion = btAryData[0];
            m_curReaderSetting.btUserDefineFrequencyInterval = btAryData[1];
            m_curReaderSetting.btUserDefineChannelQuantity = btAryData[2];
            m_curReaderSetting.nUserDefineStartFrequency = (btAryData[3] & 0xFF)
                    * 256
                    * 256
                    + (btAryData[4] & 0xFF)
                    * 256
                    + (btAryData[5] & 0xFF);
            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetBeeperMode(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetReaderTemperature(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x02) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btPlusMinus = btAryData[0];
            m_curReaderSetting.btTemperature = btAryData[1];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processReadGpioValue(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x02) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btGpio1Value = btAryData[0];
            m_curReaderSetting.btGpio2Value = btAryData[1];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processWriteGpioValue(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processSetAntConnectionDetector(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetAntConnectionDetector(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btAntDetector = btAryData[0];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetTemporaryOutputPower(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processSetReaderIdentifier(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetReaderIdentifier(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x0C) {
            m_curReaderSetting.btReadId = msgTran.getReadId();

            Arrays.fill(m_curReaderSetting.btAryReaderIdentifier, (byte) 0x00);
            System.arraycopy(btAryData, 0,
                    m_curReaderSetting.btAryReaderIdentifier, 0,
                    btAryData.length);

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetRfLinkProfile(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetRfLinkProfile(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if ((btAryData[0] & 0xFF) >= 0xD0 && (btAryData[0] & 0xFF) <= 0xD3) {
                m_curReaderSetting.btReadId = msgTran.getReadId();
                m_curReaderSetting.btRfLinkProfile = btAryData[0];

                refreshReaderSetting(btCmd, m_curReaderSetting);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processGetRfPortReturnLoss(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btReturnLoss = btAryData[0];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processInventory(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x09) {
            m_curInventoryBuffer.nCurrentAnt = btAryData[0];
            m_curInventoryBuffer.nTagCount = (btAryData[1] & 0xFF) * 256
                    + (btAryData[2] & 0xFF);
            m_curInventoryBuffer.nReadRate = (btAryData[3] & 0xFF) * 256
                    + (btAryData[4] & 0xFF);
            Logcat.d(KLog.TAG+"Print the rate", ((btAryData[3] & 0xFF) * 256 + (btAryData[4] & 0xFF)) + "::::");
            int nTotalRead = (btAryData[5] & 0xFF) * 256 * 256 * 256
                    + (btAryData[6] & 0xFF) * 256 * 256 + (btAryData[7] & 0xFF)
                    * 256 + (btAryData[8] & 0xFF);
            m_curInventoryBuffer.nDataCount = nTotalRead;
            m_curInventoryBuffer.nTotalRead += nTotalRead;
            m_curInventoryBuffer.dtEndInventory = new Date();

            refreshInventory(btCmd, m_curInventoryBuffer);
            writeLog(strCmd, ERROR.SUCCESS);

            runLoopInventroy();
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
        refreshInventory(INVENTORY_ERR_END, m_curInventoryBuffer);

        runLoopInventroy();
    }

    private void processReadTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;
            writeLog(strLog, ERROR.FAIL);
        } else {
            int nLen = btAryData.length;
            int nDataLen = (btAryData[nLen - 3] & 0xFF);
            int nEpcLen = (btAryData[2] & 0xFF) - nDataLen - 4;

            String strPC = StringTool.byteArrayToString(btAryData, 3, 2);
            String strEPC = StringTool.byteArrayToString(btAryData, 5, nEpcLen);
            String strCRC = StringTool.byteArrayToString(btAryData,
                    5 + nEpcLen, 2);
            String strData = StringTool.byteArrayToString(btAryData,
                    7 + nEpcLen, nDataLen);

            byte btTemp = btAryData[nLen - 2];
            byte btAntId = (byte) ((btTemp & 0x03) + 1);
            int nReadCount = btAryData[nLen - 1] & 0xFF;

            OperateTagBuffer.OperateTagMap tag = new OperateTagBuffer.OperateTagMap();
            tag.strPC = strPC;
            tag.strCRC = strCRC;
            tag.strEPC = strEPC;
            tag.strData = strData;
            tag.nDataLen = nDataLen;
            tag.btAntId = btAntId;
            tag.nReadCount = nReadCount;
            m_curOperateTagBuffer.lsTagList.add(tag);

            refreshOperateTag(btCmd, m_curOperateTagBuffer);
            writeLog(strCmd, ERROR.SUCCESS);
        }
    }

    private void processWriteTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;
            writeLog(strLog, ERROR.FAIL);
        } else {
            int nLen = btAryData.length;
            int nEpcLen = (btAryData[2] & 0xFF) - 4;

            if (btAryData[nLen - 3] != ERROR.SUCCESS) {
                strErrorCode = ERROR.format(btAryData[nLen - 3]);
                String strLog = strCmd
                        + mContext.getResources().getString(
                        R.string.failure_reason) + strErrorCode;

                writeLog(strLog, ERROR.FAIL);
                return;
            }
            String strPC = StringTool.byteArrayToString(btAryData, 3, 2);
            String strEPC = StringTool.byteArrayToString(btAryData, 5, nEpcLen);
            String strCRC = StringTool.byteArrayToString(btAryData,
                    5 + nEpcLen, 2);
            // add by lei.li 2016/11/10 I do not know why i fix it.
            String strData = "";
            //String strData = StringTool.byteArrayToString(btAryData, 0,
            //		btAryData.length);
            //add by lei.li 2016/1/17 I do not know why i fix it;
            byte btTemp = btAryData[nLen - 2];
            byte btAntId = (byte) ((btTemp & 0x03) + 1);
            int nReadCount = btAryData[nLen - 1] & 0xFF;

            OperateTagBuffer.OperateTagMap tag = new OperateTagBuffer.OperateTagMap();
            tag.strPC = strPC;
            tag.strCRC = strCRC;
            tag.strEPC = strEPC;
            tag.strData = strData;
            tag.nDataLen = btAryData.length;
            tag.btAntId = btAntId;
            tag.nReadCount = nReadCount;
            m_curOperateTagBuffer.lsTagList.add(tag);

            refreshOperateTag(btCmd, m_curOperateTagBuffer);
            writeLog(strCmd, ERROR.SUCCESS);
        }
    }

    /**
     * processWriteTag 与 processLockTag 返回一致。
     *
     * @param msgTran 消息包
     */
    private void processLockTag(MessageTran msgTran) {
        processWriteTag(msgTran);
    }

    /**
     * processKillTag 与 processLockTag 返回一致。
     *
     * @param msgTran 消息包
     */
    private void processKillTag(MessageTran msgTran) {
        processWriteTag(msgTran);
    }

    private void processSetAccessEpcMatch(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetAccessEpcMatch(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == 0x01) {
                writeLog(
                        mContext.getResources().getString(
                                R.string.no_match_label), ERROR.FAIL);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            if (btAryData[0] == 0x00) {
                m_curOperateTagBuffer.strAccessEpcMatch = StringTool
                        .byteArrayToString(btAryData, 2, btAryData[1] & 0xFF);

                refreshOperateTag(btCmd, m_curOperateTagBuffer);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = mContext.getResources().getString(
                        R.string.unknown_error);
            }
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;

        writeLog(strLog, ERROR.FAIL);
    }

    private void processRealTimeInventory(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
            refreshInventoryReal(INVENTORY_ERR_END, m_curInventoryBuffer);
            runLoopInventroy();
        } else if (btAryData.length == 0x07) {
            m_curInventoryBuffer.nReadRate = (btAryData[1] & 0xFF) * 256 + (btAryData[2] & 0xFF);
            m_curInventoryBuffer.nDataCount = (btAryData[3] & 0xFF) * 256 * 256
                    * 256 + (btAryData[4] & 0xFF) * 256 * 256
                    + (btAryData[5] & 0xFF) * 256 + (btAryData[6] & 0xFF);
            m_curInventoryBuffer.nCurrentAnt = btAryData[0] + 1;
            writeLog(strCmd, ERROR.SUCCESS);
            refreshInventoryReal(INVENTORY_END, m_curInventoryBuffer);
            runLoopInventroy();
        } else {//ly-增加蜂鸣器控制----------------------------------
            m_nTotal++;
            int nLength = btAryData.length;
            int nEpcLength = nLength - 4;

            //add by lei.li 2017/1/11
            String strEPC = "";
            if (nEpcLength != 0) {
                strEPC = StringTool.byteArrayToString(btAryData, 3, nEpcLength);
            }
            //add by lei.li 2017/1/11
            String strPC = StringTool.byteArrayToString(btAryData, 1, 2);
            String strRSSI = String.valueOf(btAryData[nLength - 1] & 0xFF);
            setMaxMinRSSI(btAryData[nLength - 1] & 0xFF);
            byte btTemp = btAryData[0];
            byte btAntId = (byte) ((btTemp & 0x03) + 1);
            m_curInventoryBuffer.nCurrentAnt = btAntId & 0xFF;

            byte btFreq = (byte) ((btTemp & 0xFF) >> 2);
            String strFreq = getFreqString(btFreq);

            InventoryBuffer.InventoryTagMap tag = null;
            Integer findIndex = m_curInventoryBuffer.dtIndexMap.get(strEPC);
            if (findIndex == null) {
                tag = new InventoryBuffer.InventoryTagMap();
                tag.strPC = strPC;
                tag.strEPC = strEPC;
                tag.strRSSI = strRSSI;
                tag.nReadCount = 1;
                tag.strFreq = strFreq;
                m_curInventoryBuffer.lsTagList.add(tag);
                m_curInventoryBuffer.dtIndexMap.put(strEPC,
                        m_curInventoryBuffer.lsTagList.size() - 1);
            } else {
                tag = m_curInventoryBuffer.lsTagList.get(findIndex);
                tag.strRSSI = strRSSI;
                tag.nReadCount++;
                tag.strFreq = strFreq;
            }
            if (m_curReaderSetting.btBeeperMode != 0) {
                //MainActivity.instance.playSounds(1,1);//ly--------------------------
            }
            m_curInventoryBuffer.dtEndInventory = new Date();
            refreshInventoryReal(btCmd, m_curInventoryBuffer);
        }
    }

    private void processFastSwitchInventory(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
            refreshFastSwitch(INVENTORY_ERR_END, m_curInventoryBuffer);
            runLoopFastSwitch();
        } else if (btAryData.length == 0x02) {
            strErrorCode = ERROR.format(btAryData[1]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode
                    + "--"
                    + mContext.getResources().getString(R.string.antenna)
                    + ((btAryData[0] & 0xFF) + 1);

            writeLog(strLog, ERROR.FAIL);
        } else if (btAryData.length == 0x07) {
            // m_nSwitchTotal, m_nSwitchTime
            int nSwitchTotal = (btAryData[0] & 0xFF) * 255 * 255
                    + (btAryData[1] & 0xFF) * 255 + (btAryData[2] & 0xFF);
            int nSwitchTime = (btAryData[3] & 0xFF) * 255 * 255 * 255
                    + (btAryData[4] & 0xFF) * 255 * 255 + (btAryData[5] & 0xFF)
                    * 255 + (btAryData[6] & 0xFF);

            m_curInventoryBuffer.nDataCount = nSwitchTotal;
            m_curInventoryBuffer.nCommandDuration = nSwitchTime;

            writeLog(strCmd, ERROR.SUCCESS);
            refreshFastSwitch(INVENTORY_END, m_curInventoryBuffer);
            runLoopFastSwitch();
        } else {
            m_nTotal++;
            int nLength = btAryData.length;
            int nEpcLength = nLength - 4;

            String strEPC = StringTool.byteArrayToString(btAryData, 3,
                    nEpcLength);
            String strPC = StringTool.byteArrayToString(btAryData, 1, 2);
            String strRSSI = String.valueOf(btAryData[nLength - 1] & 0xFF);
            setMaxMinRSSI(btAryData[nLength - 1] & 0xFF);
            byte btTemp = btAryData[0];
            byte btAntId = (byte) ((btTemp & 0x03) + 1);
            m_curInventoryBuffer.nCurrentAnt = btAntId & 0xFF;
            // String strAntId = String.valueOf(btAntId & 0xFF);

            byte btFreq = (byte) ((btTemp & 0xFF) >> 2);
            String strFreq = getFreqString(btFreq);

            InventoryBuffer.InventoryTagMap tag = null;
            Integer findIndex = m_curInventoryBuffer.dtIndexMap.get(strEPC);
            if (findIndex == null) {
                tag = new InventoryBuffer.InventoryTagMap();
                tag.strPC = strPC;
                tag.strEPC = strEPC;
                tag.strRSSI = strRSSI;
                tag.nReadCount = 1;
                tag.strFreq = strFreq;
                tag.nAnt1 = 0;
                tag.nAnt2 = 0;
                tag.nAnt3 = 0;
                tag.nAnt4 = 0;

                switch (btAntId) {
                    case 0x01:
                        tag.nAnt1 = 1;
                        break;
                    case 0x02:
                        tag.nAnt2 = 1;
                        break;
                    case 0x03:
                        tag.nAnt3 = 1;
                        break;
                    case 0x04:
                        tag.nAnt4 = 1;
                        break;
                    default:
                        break;
                }
                m_curInventoryBuffer.lsTagList.add(tag);
                m_curInventoryBuffer.dtIndexMap.put(strEPC,
                        m_curInventoryBuffer.lsTagList.size() - 1);
            } else {
                tag = m_curInventoryBuffer.lsTagList.get(findIndex);
                tag.strRSSI = strRSSI;
                tag.nReadCount++;
                tag.strFreq = strFreq;
                switch (btAntId) {
                    case 0x01:
                        tag.nAnt1++;
                        break;
                    case 0x02:
                        tag.nAnt2++;
                        break;
                    case 0x03:
                        tag.nAnt3++;
                        break;
                    case 0x04:
                        tag.nAnt4++;
                        break;
                    default:
                        break;
                }
            }

            m_curInventoryBuffer.dtEndInventory = new Date();
            refreshFastSwitch(btCmd, m_curInventoryBuffer);
        }

    }

    /**
     * processCustomizedSessionTargetInventory and processRealTimeInventory return consistent.
     *
     * @param msgTran Packet contents
     */
    private void processCustomizedSessionTargetInventory(MessageTran msgTran) {
        processRealTimeInventory(msgTran);
    }

    private void processSetImpinjFastTid(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processSetAndSaveImpinjFastTid(MessageTran msgTran) {
        processSet(msgTran);
    }

    private void processGetImpinjFastTid(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == 0x00 || (btAryData[0] & 0xFF) == 0x8D) {
                m_curReaderSetting.btReadId = msgTran.getReadId();
                m_curReaderSetting.btMonzaStatus = btAryData[0];

                refreshReaderSetting(btCmd, m_curReaderSetting);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processSetWorkMode(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btModeStatus = btAryData[0];

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processGetWorkMode(MessageTran msgTran) {//ly
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == 0x00 || btAryData[0] == 0x01) {
                m_curReaderSetting.btReadId = msgTran.getReadId();
                m_curReaderSetting.btModeStatus = btAryData[0];

                refreshReaderSetting(btCmd, m_curReaderSetting);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processGetAntPhysicalConnectionStatus(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x02) {
            m_curReaderSetting.btReadId = msgTran.getReadId();
            m_curReaderSetting.btAntStatus = Converter.byteToBitArray(btAryData[1]);

            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void processISO180006BInventory(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if ((btAryData[0] & 0xFF) != 0xFF) {
                strErrorCode = ERROR.format(btAryData[0]);
                String strLog = strCmd
                        + mContext.getResources().getString(
                        R.string.failure_reason) + strErrorCode;

                writeLog(strLog, ERROR.FAIL);
                refreshISO180006B(INVENTORY_ERR_END,
                        m_curOperateTagISO18000Buffer);
            }
        } else if (btAryData.length == 0x09) {
            String strUID = StringTool.byteArrayToString(btAryData, 1, 8);

            ISO180006BOperateTagBuffer.ISO180006BOperateTagMap tag = null;
            Integer findIndex = m_curOperateTagISO18000Buffer.dtIndexMap
                    .get(strUID);
            if (findIndex == null) {
                tag = new ISO180006BOperateTagBuffer.ISO180006BOperateTagMap();
                tag.btAntId = btAryData[0];
                tag.strUID = strUID;
                tag.nTotal = 1;
                m_curOperateTagISO18000Buffer.lsTagList.add(tag);
                m_curOperateTagISO18000Buffer.dtIndexMap.put(strUID,
                        m_curOperateTagISO18000Buffer.lsTagList.size() - 1);
            } else {
                tag = m_curOperateTagISO18000Buffer.lsTagList.get(findIndex);
                tag.nTotal++;
            }

            refreshISO180006B(btCmd, m_curOperateTagISO18000Buffer);
        } else if (btAryData.length == 0x02) {
            m_curOperateTagISO18000Buffer.nTagCount = btAryData[1] & 0xFF;

            refreshISO180006B(INVENTORY_END, m_curOperateTagISO18000Buffer);
            writeLog(strCmd, ERROR.SUCCESS);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);

            refreshISO180006B(INVENTORY_ERR_END, m_curOperateTagISO18000Buffer);
        }
    }

    private void processISO180006BReadTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
        } else {
            String strData = StringTool.byteArrayToString(btAryData, 1,
                    btAryData.length - 1);

            m_curOperateTagISO18000Buffer.btAntId = btAryData[0];
            m_curOperateTagISO18000Buffer.strReadData = strData;

            refreshISO180006B(btCmd, m_curOperateTagISO18000Buffer);

            writeLog(strCmd, ERROR.SUCCESS);
        }
    }

    private void processISO180006BWriteTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
        } else {
            m_curOperateTagISO18000Buffer.btAntId = btAryData[0];
            m_curOperateTagISO18000Buffer.btWriteLength = btAryData[1];

            refreshISO180006B(btCmd, m_curOperateTagISO18000Buffer);
            String strLog = strCmd
                    + ": "
                    + mContext.getResources().getString(
                    R.string.write_success_l) + (btAryData[1] & 0xFF)
                    + mContext.getResources().getString(R.string.byte_l);
            writeLog(strLog, ERROR.SUCCESS);
            // RunLoopISO18000(Convert.ToInt32(msgTran.AryData[1]));
        }
    }

    private void processISO180006BLockTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
        } else {
            m_curOperateTagISO18000Buffer.btAntId = btAryData[0];
            m_curOperateTagISO18000Buffer.btStatus = btAryData[1];

            refreshISO180006B(btCmd, m_curOperateTagISO18000Buffer);
            String strLog = "";
            switch (btAryData[1] & 0xFF) {
                case 0x00:
                    strLog = strCmd + ": " + "Successfully locked";
                    break;
                case 0xFE:
                    strLog = strCmd + ": " + "Locked State";
                    break;
                case 0xFF:
                    strLog = strCmd + ": " + "Unable to lock";
                    break;
                default:
                    break;
            }

            writeLog(strLog, ERROR.SUCCESS);

        }
    }

    private void processISO180006BQueryLockTag(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
        } else {
            m_curOperateTagISO18000Buffer.btAntId = btAryData[0];
            m_curOperateTagISO18000Buffer.btStatus = btAryData[1];

            refreshISO180006B(btCmd, m_curOperateTagISO18000Buffer);

            writeLog(strCmd, ERROR.SUCCESS);
        }
    }

    private void processGetInventoryBuffer(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
            String strLog = strCmd
                    + mContext.getResources()
                    .getString(R.string.failure_reason) + strErrorCode;

            writeLog(strLog, ERROR.FAIL);
        } else {
            int nDataLen = btAryData.length;
            int nEpcLen = (btAryData[2] & 0xFF) - 4;

            String strPC = StringTool.byteArrayToString(btAryData, 3, 2);
            String strEPC = StringTool.byteArrayToString(btAryData, 5, nEpcLen);
            String strCRC = StringTool.byteArrayToString(btAryData,
                    5 + nEpcLen, 2);
            String strRSSI = String.valueOf(btAryData[nDataLen - 3] & 0xFF);
            setMaxMinRSSI(btAryData[nDataLen - 3] & 0xFF);
            byte btTemp = btAryData[nDataLen - 2];
            byte btAntId = (byte) ((btTemp & 0x03) + 1);
            int nReadCount = btAryData[nDataLen - 1] & 0xFF;

            InventoryBuffer.InventoryTagMap tag = new InventoryBuffer.InventoryTagMap();
            tag.strPC = strPC;
            tag.strCRC = strCRC;
            tag.strEPC = strEPC;
            tag.btAntId = btAntId;
            tag.strRSSI = strRSSI;
            tag.nReadCount = nReadCount;
            m_curInventoryBuffer.lsTagList.add(tag);
            m_curInventoryBuffer.dtIndexMap.put(strEPC,
                    m_curInventoryBuffer.lsTagList.size() - 1);

            refreshInventory(btCmd, m_curInventoryBuffer);
            writeLog(strCmd, ERROR.SUCCESS);
        }
    }

    /**
     * processGetAndResetInventoryBuffer and processGetInventoryBuffer return consistent.
     *
     * @param msgTran Packet contents
     */
    private void processGetAndResetInventoryBuffer(MessageTran msgTran) {
        processGetInventoryBuffer(msgTran);
    }

    private void processGetInventoryBufferTagCount(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x02) {
            m_curInventoryBuffer.nTagCount = (btAryData[0] & 0xFF) * 256
                    + (btAryData[1] & 0xFF);

            refreshInventory(btCmd, m_curInventoryBuffer);
            String strLog = strCmd + "："
                    + String.valueOf(m_curInventoryBuffer.nTagCount);
            writeLog(strLog, ERROR.FAIL);
            return;
        } else if (btAryData.length == 0x01) {
            strErrorCode = ERROR.format(btAryData[0]);
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;

        writeLog(strLog, ERROR.FAIL);
    }

    private void processResetInventoryBuffer(MessageTran msgTran) {
        byte btCmd = msgTran.getCmd();
        byte[] btAryData = msgTran.getAryData();
        String strCmd = CMD.format(btCmd);
        String strErrorCode = "";

        if (btAryData.length == 0x01) {
            if (btAryData[0] == ERROR.SUCCESS) {
                refreshInventory(btCmd, m_curInventoryBuffer);
                writeLog(strCmd, ERROR.SUCCESS);
                return;
            } else {
                strErrorCode = ERROR.format(btAryData[0]);
            }
        } else {
            strErrorCode = mContext.getResources().getString(
                    R.string.unknown_error);
        }

        String strLog = strCmd
                + mContext.getResources().getString(R.string.failure_reason)
                + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }

    private void setMaxMinRSSI(int nRSSI) {
        if (m_curInventoryBuffer.nMaxRSSI < nRSSI) {
            m_curInventoryBuffer.nMaxRSSI = nRSSI;
        }

        if (m_curInventoryBuffer.nMinRSSI == 0) {
            m_curInventoryBuffer.nMinRSSI = nRSSI;
        } else if (m_curInventoryBuffer.nMinRSSI > nRSSI) {
            m_curInventoryBuffer.nMinRSSI = nRSSI;
        }
    }

    private String getFreqString(byte btFreq) {
        if (m_curReaderSetting.btRegion == 4) {
            float nExtraFrequency = (float) (btFreq & 0xFF)
                    * (m_curReaderSetting.btUserDefineFrequencyInterval & 0xFF)
                    * 10;
            float nstartFrequency = (float) ((float) (m_curReaderSetting.nUserDefineStartFrequency & 0xFF)) / 1000;
            float nStart = (float) (nstartFrequency + nExtraFrequency / 1000);
            String strTemp = String.format("%.3f", nStart);

            return strTemp;
        } else {
            if ((btFreq & 0xFF) < 0x07) {
                float nStart = (float) (865.00f + (float) (btFreq & 0xFF) * 0.5f);
                String strTemp = String.format("%.2f", nStart);

                return strTemp;
            } else {
                float nStart = (float) (902.00f + ((float) (btFreq & 0xFF) - 7) * 0.5f);
                String strTemp = String.format("%.2f", nStart);

                return strTemp;
            }
        }
    }

    private void processTagMask(MessageTran msgTran) {
        String strCmd = mContext.getResources().getString(R.string.operate_mask);
        byte[] btAryData = msgTran.getAryData();
        String strErrorCode = "";
        if (btAryData.length == 1) {
            if (btAryData[0] == (byte) 0x10) {
                writeLog(mContext.getResources().getString(R.string.command_succeeded), 0x10);
                return;
            } else if (btAryData[1] == (byte) 0x41) {
                strErrorCode = mContext.getResources().getString(R.string.parameter_invalid_mask);
            } else {
                strErrorCode = "Unknown Error";
            }
        } else {
            if (btAryData.length > 7) {

                m_curReaderSetting.btsGetMaskValue = msgTran.getAryData();
                refreshReaderSetting(msgTran.getCmd(), m_curReaderSetting);
                writeLog(mContext.getResources().getString(R.string.get_mask_success), 0x10);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        String strLog = strCmd + mContext.getResources().getString(R.string.failure_reason) + strErrorCode;
        writeLog(strLog, 0x11);
    }


    public void runLoopInventroy() {
        //循环设置天线指令和实时盘存指令（先设置天线指令再设置盘存指令）
        if (m_curInventoryBuffer.nIndexAntenna < m_curInventoryBuffer.lAntenna.size() || m_curInventoryBuffer.nCommond == 0) {
            if (m_curInventoryBuffer.nCommond == 0) {
                m_curInventoryBuffer.nCommond = 1;
                if (m_curInventoryBuffer.bLoopInventoryReal) { //m_bLockTab = true;
                    //btnInventory.Enabled = false;
                    if (m_curInventoryBuffer.bLoopCustomizedSession) {
                        //自定义Session和Inventoried Flag
                        mReader.customizedSessionTargetInventory(m_curReaderSetting.btReadId,
                                m_curInventoryBuffer.btSession, m_curInventoryBuffer.btTarget,
                                m_curInventoryBuffer.btRepeat);
                    } else { //实时盘存
                        mReader.realTimeInventory(m_curReaderSetting.btReadId, m_curInventoryBuffer.btRepeat);
                    }
                } else if (m_curInventoryBuffer.bLoopInventory) {
                    mReader.inventory(m_curReaderSetting.btReadId, m_curInventoryBuffer.btRepeat);
                }
            } else {
                m_curInventoryBuffer.nCommond = 0;
                m_curInventoryBuffer.nIndexAntenna = (m_curInventoryBuffer.nIndexAntenna + 1) % m_curInventoryBuffer.lAntenna.size();

                byte btWorkAntenna = m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
                mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
                m_curReaderSetting.btWorkAntenna = btWorkAntenna;
            }
        }

        // The codes before are removed.
        else if (m_curInventoryBuffer.bLoopInventory || m_curInventoryBuffer.bLoopInventoryReal) {
            m_curInventoryBuffer.nIndexAntenna = 0;
            m_curInventoryBuffer.nCommond = 0;

            byte btWorkAntenna = m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
            mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
            m_curReaderSetting.btWorkAntenna = 0;
            if (m_curInventoryBuffer.bLoopInventoryReal) {
                // m_bLockTab = true;
                // btnInventory.Enabled = false;
                if (m_curInventoryBuffer.bLoopCustomizedSession) {
                    // Flag
                    mReader.customizedSessionTargetInventory(
                            m_curReaderSetting.btReadId,
                            m_curInventoryBuffer.btSession,
                            m_curInventoryBuffer.btTarget,
                            m_curInventoryBuffer.btRepeat);
                } else {
                    mReader.realTimeInventory(m_curReaderSetting.btReadId, m_curInventoryBuffer.btRepeat);

                }
            } else if (m_curInventoryBuffer.bLoopInventory) {
                mReader.inventory(m_curReaderSetting.btReadId, m_curInventoryBuffer.btRepeat);
            }
        }
    }

    private void runLoopFastSwitch() {
        if (m_curInventoryBuffer.bLoopInventory) {
            mReader.fastSwitchAntInventory(m_curReaderSetting.btReadId,
                    m_curInventoryBuffer.btA, m_curInventoryBuffer.btStayA,
                    m_curInventoryBuffer.btB, m_curInventoryBuffer.btStayB,
                    m_curInventoryBuffer.btC, m_curInventoryBuffer.btStayC,
                    m_curInventoryBuffer.btD, m_curInventoryBuffer.btStayD,
                    m_curInventoryBuffer.btInterval,
                    m_curInventoryBuffer.btFastRepeat);
        }
    }


}
