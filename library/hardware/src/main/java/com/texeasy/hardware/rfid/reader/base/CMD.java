package com.texeasy.hardware.rfid.reader.base;

import com.example.common.base.BaseApplication;
import com.texeasy.hardware.R;

/**
 * Regarding UHF return code and description, please refer to Serial_Protocol_User's_Guide_V2.38_en
 *
 * @author Administrator
 */
public class CMD {
    public final static byte GET_ANT_PHYSICAL_CONNECTION_STATUS = 0x53;//获取天线连接状态
    public final static byte SET_WORK_MODE = 0x54;//设置自定义实时盘存的工作模式。此命令仅对自定义实时盘存起作用。
    public final static byte GET_WORK_MODE = 0x55;//读取自定义实时盘存的工作模式。
    public final static byte RESET = 0x70;//复位读写器
    public final static byte SET_UART_BAUDRATE = 0x71;//设置串口通讯波特率
    public final static byte GET_FIRMWARE_VERSION = 0x72;//读取读写器固件版本
    public final static byte SET_READER_ADDRESS = 0x73;//设置读写器地址
    public final static byte SET_WORK_ANTENNA = 0x74;//设置读写器工作天线
    public final static byte GET_WORK_ANTENNA = 0x75;//查询当前天线工作天线
    public final static byte SET_OUTPUT_POWER = 0x76;//设置读写器射频输出功率：此命令耗时将超过100mS
    public final static byte GET_OUTPUT_POWER = 0x77;//查询读写器当前输出功率
    public final static byte SET_FREQUENCY_REGION = 0x78;//设置读写器工作频率范围
    public final static byte GET_FREQUENCY_REGION = 0x79;//查询读写器工作频率范围
    public final static byte SET_BEEPER_MODE = 0x7A;//设置蜂鸣器状态：读到一张标签后蜂鸣器鸣响，会占用大量处理器时间，若此选项打开，将会明显影响到读多标签（防冲突算法）的性能，此选项应作为测试功能选用。
    public final static byte GET_READER_TEMPERATURE = 0x7B;//查询当前设备的工作温度
    public final static byte READ_GPIO_VALUE = 0x60;//读取GPIO电平
    public final static byte WRITE_GPIO_VALUE = 0x61;//设置GPIO电平
    public final static byte SET_ANT_CONNECTION_DETECTOR = 0x62;//设置天线连接检测器状态
    public final static byte GET_ANT_CONNECTION_DETECTOR = 0x63;//读取天线连接检测器状态
    public final static byte SET_TEMPORARY_OUTPUT_POWER = 0x66;//设置读写器临时射频输出功率
    public final static byte SET_READER_IDENTIFIER = 0x67;//设置读写器识别码
    public final static byte GET_READER_IDENTIFIER = 0x68;//读取读写器识别码
    public final static byte SET_RF_LINK_PROFILE = 0x69;//设置射频链路的通讯速率
    public final static byte GET_RF_LINK_PROFILE = 0x6A;//读取射频链路的通讯速率
    public final static byte GET_RF_PORT_RETURN_LOSS = 0x7E;//测量天线端口的回波损耗
    public final static byte INVENTORY = (byte) 0x80;//盘存标签
    public final static byte READ_TAG = (byte) 0x81;//读标签
    public final static byte WRITE_TAG = (byte) 0x82;//写标签
    public final static byte LOCK_TAG = (byte) 0x83;//锁定标签
    public final static byte KILL_TAG = (byte) 0x84;//灭活标签
    public final static byte SET_ACCESS_EPC_MATCH = (byte) 0x85;//匹配ACCESS操作的EPC号
    public final static byte GET_ACCESS_EPC_MATCH = (byte) 0x86;//查询匹配的EPC状态
    public final static byte REAL_TIME_INVENTORY = (byte) 0x89;//盘存标签(实时上传标签数据)
    public final static byte FAST_SWITCH_ANT_INVENTORY = (byte) 0x8A;//快速轮询多个天线盘存标签
    public final static byte CUSTOMIZED_SESSION_TARGET_INVENTORY = (byte) 0x8B;//自定义session和target盘存
    public final static byte SET_IMPINJ_FAST_TID = (byte) 0x8C;//设置Monza标签快速读TID(设置不被保存至内部FLASH)
    public final static byte SET_AND_SAVE_IMPINJ_FAST_TID = (byte) 0x8D;//设置Monza标签快速读TID(设置被保存至内部FLASH)
    public final static byte GET_IMPINJ_FAST_TID = (byte) 0x8E;//查询当前的快速TID设置
    public final static byte ISO18000_6B_INVENTORY = (byte) 0xB0;//
    public final static byte ISO18000_6B_READ_TAG = (byte) 0xB1;//
    public final static byte ISO18000_6B_WRITE_TAG = (byte) 0xB2;//
    public final static byte ISO18000_6B_LOCK_TAG = (byte) 0xB3;//
    public final static byte ISO18000_6B_QUERY_LOCK_TAG = (byte) 0xB4;//
    //缓存操作命令
    public final static byte GET_INVENTORY_BUFFER = (byte) 0x90;//提取标签数据保留缓存备份
    public final static byte GET_AND_RESET_INVENTORY_BUFFER = (byte) 0x91;//提取标签数据并删除缓存
    public final static byte GET_INVENTORY_BUFFER_TAG_COUNT = (byte) 0x92;//查询缓存中已读标签个数
    public final static byte RESET_INVENTORY_BUFFER = (byte) 0x93;//清空标签数据缓存
    public final static byte OPERATE_TAG_MASK = (byte) 0x98;//

    public static String format(byte btCmd) {
        String strCmd = "";
        switch (btCmd) {
            case RESET:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.reset);
                break;
            case SET_UART_BAUDRATE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_uart_baudrate);
                break;
            case GET_FIRMWARE_VERSION:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_firmware_version);
                break;
            case SET_READER_ADDRESS:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_reader_add);
                break;
            case SET_WORK_ANTENNA:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_work_ant);
                break;
            case GET_WORK_ANTENNA:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_work_ant);
                break;
            case SET_OUTPUT_POWER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_output_power);
                break;
            case GET_OUTPUT_POWER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_output_power);
                break;
            case SET_FREQUENCY_REGION:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_freq_reg);
                break;
            case GET_FREQUENCY_REGION:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_freq_reg);
                break;
            case SET_BEEPER_MODE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_beeper_mode);
                break;
            case GET_READER_TEMPERATURE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_rader_temp);
                break;
            case READ_GPIO_VALUE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.read_gpio_value);
                break;
            case WRITE_GPIO_VALUE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.write_gpio_value);
                break;
            case SET_ANT_CONNECTION_DETECTOR:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_ant_conn);
                break;
            case GET_ANT_CONNECTION_DETECTOR:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_ant_conn);
                break;
            case SET_TEMPORARY_OUTPUT_POWER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_temp_output_power);
                break;
            case SET_READER_IDENTIFIER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_reader_identi);
                break;
            case GET_READER_IDENTIFIER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_reader_identi);
                break;
            case SET_RF_LINK_PROFILE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_rf_link_pro);
                break;
            case GET_RF_LINK_PROFILE:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_rf_link_pro);
                break;
            case GET_RF_PORT_RETURN_LOSS:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_rf_port);
                break;
            case INVENTORY:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.inventory);
                break;
            case READ_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.read_tag_c);
                break;
            case WRITE_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.write_tag_c);
                break;
            case LOCK_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.lock_tag_c);
                break;
            case KILL_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.kill_tag_c);
                break;
            case SET_ACCESS_EPC_MATCH:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_access_epc_match);
                break;
            case GET_ACCESS_EPC_MATCH:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_access_epc_match);
                break;
            case REAL_TIME_INVENTORY:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.real_time_inventory);
                break;
            case FAST_SWITCH_ANT_INVENTORY:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.fast_switch_ant_inv);
                break;
            case CUSTOMIZED_SESSION_TARGET_INVENTORY:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.customized_session);
                break;
            case SET_IMPINJ_FAST_TID:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_impinj);
                break;
            case SET_AND_SAVE_IMPINJ_FAST_TID:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.set_and_save_impinj);
                break;
            case GET_IMPINJ_FAST_TID:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_imping);
                break;
            case ISO18000_6B_INVENTORY:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.iso_B_inv);
                break;
            case ISO18000_6B_READ_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.iso_B_read);
                break;
            case ISO18000_6B_WRITE_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.iso_b_write_tag);
                break;
            case ISO18000_6B_LOCK_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.iso_b_lock_tag);
                break;
            case ISO18000_6B_QUERY_LOCK_TAG:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.iso_b_query_lock_tag);
                break;
            case GET_INVENTORY_BUFFER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_inventory_buff);
                break;
            case GET_AND_RESET_INVENTORY_BUFFER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_ant_reset_inv);
                break;
            case GET_INVENTORY_BUFFER_TAG_COUNT:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.get_inventory_buffer_tag_count);
                break;
            case RESET_INVENTORY_BUFFER:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.reset_inventory_buff);
                break;
            default:
                strCmd = BaseApplication.getInstance().getResources().getString(R.string.unknown_operate);
                break;
        }
        return strCmd;
    }
}
