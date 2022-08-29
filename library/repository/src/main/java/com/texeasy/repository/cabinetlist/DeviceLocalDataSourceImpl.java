package com.texeasy.repository.cabinetlist;

import android.text.TextUtils;

import com.example.common.utils.SPUtils;
import com.google.gson.Gson;
import com.texeasy.repository.entity.DeviceInfo;

public class DeviceLocalDataSourceImpl implements DeviceLocalDataSource {
    private volatile static DeviceLocalDataSourceImpl INSTANCE = null;
    private static DeviceInfo deviceInfo;

    public static DeviceLocalDataSourceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (DeviceLocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceLocalDataSourceImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private DeviceLocalDataSourceImpl() {
        //数据库Helper构建
    }

    @Override
    public DeviceInfo getLocalDeviceInfo() {
        if (deviceInfo == null) {
            try {
                String sDeviceInfo = SPUtils.getInstance().getString("DeviceInfo");
                if (!TextUtils.isEmpty(sDeviceInfo)) {
                    deviceInfo = new Gson().fromJson(sDeviceInfo, DeviceInfo.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deviceInfo != null ? deviceInfo : new DeviceInfo();
    }
}
