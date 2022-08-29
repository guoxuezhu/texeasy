package com.texeasy.repository.cabinetlist;

import com.texeasy.repository.entity.DeviceInfo;

public interface DeviceLocalDataSource {
    /**
     * 获取当前设备的本地信息
     *
     * @return
     */
    default DeviceInfo getLocalDeviceInfo() {
        return null;
    }
}
