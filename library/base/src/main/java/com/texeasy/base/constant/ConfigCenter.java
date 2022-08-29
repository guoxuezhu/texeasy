package com.texeasy.base.constant;


import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.example.common.base.BaseApplication;
import com.example.common.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.texeasy.base.entity.BasicSettingInfo;
import com.texeasy.base.entity.DeviceSettingInfo;

/**
 * Created by caozhihuang
 */
public class ConfigCenter extends BaseObservable {
    private static String CONFIG_INFO = "config.info";

    //用户信息
    public ObservableField<String> token = new ObservableField<>("");
    //设备信息
    public ObservableField<Integer> heartBeat = new ObservableField<>(5);
    public ObservableField<Boolean> isOpenVoice = new ObservableField<>(false);
    public ObservableField<Boolean> isSelfCheck = new ObservableField<>(false);
    public ObservableField<Boolean> isDebug = new ObservableField<>(false);
    public ObservableField<Boolean> camera90 = new ObservableField<>(false);//相机90度或270度
    public ObservableField<String> comName = new ObservableField<>("/dev/ttyS4");
    public ObservableField<Integer> rfidScanTime = new ObservableField<>(5);
    //基础信息
    public ObservableField<String> serverIp = new ObservableField<>("shoe".equals(BaseApplication.getFlavor()) ? "47.104.189.179" : "47.105.160.11");
    public ObservableField<String> serverPort = new ObservableField<>("shoe".equals(BaseApplication.getFlavor()) ? "11914" : "11913");
    public ObservableField<String> deviceCode = new ObservableField<>("shoe".equals(BaseApplication.getFlavor()) ? "TQSFXCS001" : "1");
    public ObservableField<String> socketKey = new ObservableField<>("1");


    private ConfigCenter() {
    }

    private static class ConfigCenterImpl {
        private static ConfigCenter instance = new ConfigCenter();
    }

    public static ConfigCenter newInstance() {
        return ConfigCenterImpl.instance;
    }

    /**
     * 存储设备信息
     */
    public synchronized static void putConfigInfo() {
        Gson gson = new Gson();
        SPUtils.getInstance().put(CONFIG_INFO, gson.toJson(newInstance()));
    }

    /**
     * 加载设备信息
     */
    public static void getConfigInfo() {
        String configInfo = SPUtils.getInstance().getString(CONFIG_INFO);
        if (!TextUtils.isEmpty(configInfo)) {
            try {
                Gson gson = new Gson();
                ConfigCenter configCenter = gson.fromJson(configInfo, new TypeToken<ConfigCenter>() {
                }.getType());
                configCenter.clone(ConfigCenter.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                ConfigCenter.newInstance();
            }
        } else {
            ConfigCenter.newInstance();
        }
    }

    /**
     * 更新本地设备信息
     *
     * @param info
     */
    public static void updateDeviceSettingInfo(DeviceSettingInfo info) {
        ConfigCenter instance = ConfigCenter.newInstance();
        instance.setToken(ConfigCenter.newInstance().token.get());
        instance.setHeartBeat(info.getHeartBeat());
        instance.setIsOpenVoice(info.isOpenVoice());
        instance.setIsSelfCheck(info.isSelfCheck());
        instance.setIsDebug(info.isDebug());
        instance.setCamera90(info.getCamera90());
        instance.setComName(info.getComName());
        instance.setRfidScanTime(info.getRfidScanTime());
        ConfigCenter.putConfigInfo();
    }

    /**
     * 更新本地基础信息
     *
     * @param info
     */
    public static void updateBasicSettingInfo(BasicSettingInfo info) {
        ConfigCenter instance = ConfigCenter.newInstance();
        instance.setServerIp(info.getServerIp());
        instance.setServerPort(info.getServerPort());
        instance.setDeviceCode(info.getDeviceCode());
        instance.setSocketKey(info.getSocketKey());
        ConfigCenter.putConfigInfo();
    }

    public void setToken(String token) {
        this.token.set(token);
        BaseApplication.setAuthToken(token);
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat.set(heartBeat);
    }

    public void setIsOpenVoice(boolean isOpenVoice) {
        this.isOpenVoice.set(isOpenVoice);
    }

    public void setIsSelfCheck(boolean isSelfCheck) {
        this.isSelfCheck.set(isSelfCheck);
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug.set(isDebug);
    }

    public void setCamera90(boolean camera90) {
        this.camera90.set(camera90);
    }

    public void setComName(String comName) {
        this.comName.set(comName);
    }

    public void setRfidScanTime(int rfidScanTime) {
        this.rfidScanTime.set(rfidScanTime);
    }

    public void setServerIp(String serverIp) {
        this.serverIp.set(serverIp);
    }

    public void setServerPort(String serverPort) {
        this.serverPort.set(serverPort);
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode.set(deviceCode);
    }

    public void setSocketKey(String socketKey) {
        this.socketKey.set(socketKey);
    }

    public void clone(ConfigCenter obj) {
        if (heartBeat != null) {
            obj.setHeartBeat(heartBeat.get());
        }
        if (isOpenVoice != null) {
            obj.setIsOpenVoice(isOpenVoice.get());
        }
        if (isSelfCheck != null) {
            obj.setIsSelfCheck(isSelfCheck.get());
        }
        if (isDebug != null) {
            obj.setIsDebug(isDebug.get());
        }
        if (camera90 != null) {
            obj.setCamera90(camera90.get());
        }
        if (comName != null) {
            obj.setComName(comName.get());
        }
        if (rfidScanTime != null) {
            obj.setRfidScanTime(rfidScanTime.get());
        }
        if (token != null) {
            obj.setToken(token.get());
        }
        if (serverIp != null) {
            obj.setServerIp(serverIp.get());
        }
        if (serverPort != null) {
            obj.setServerPort(serverPort.get());
        }
        if (deviceCode != null) {
            obj.setDeviceCode(deviceCode.get());
        }
        if (socketKey != null) {
            obj.setSocketKey(socketKey.get());
        }
    }
}
