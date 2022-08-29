package com.texeasy.repository.cabinetlist;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.common.base.BaseModel;
import com.example.common.http.OnResponseListener;
import com.texeasy.repository.entity.AppVersionReq;
import com.texeasy.repository.entity.AppVersionResp;
import com.texeasy.repository.entity.DeviceInfo;
import com.texeasy.repository.entity.DeviceInfoReq;
import com.texeasy.repository.entity.DoorInfo;
import com.texeasy.repository.entity.DoorInfoReq;
import com.texeasy.repository.entity.HeartbeatInfo;
import com.texeasy.repository.entity.LinenInDoorInfo;
import com.texeasy.repository.entity.LinenInDoorInfoReq;
import com.texeasy.repository.entity.LinenInfo;
import com.texeasy.repository.entity.LinenOperationRecordsReq;
import com.texeasy.repository.entity.UpdateDeviceInfo;

import java.util.List;

import io.reactivex.Observer;

/**
 * MVVM的Model层，统一模块的数据仓库，包含网络数据和本地数据
 */
public class DeviceRepository extends BaseModel implements DeviceRemoteDataSource, DeviceLocalDataSource {
    private volatile static DeviceRepository INSTANCE = null;
    private final DeviceRemoteDataSource mDeviceRemoteDataSource;

    private final DeviceLocalDataSource mDeviceLocalDataSource;

    private DeviceRepository(@NonNull DeviceRemoteDataSource deviceRemoteDataSource,
                             @NonNull DeviceLocalDataSource deviceLocalDataSource) {
        this.mDeviceRemoteDataSource = deviceRemoteDataSource;
        this.mDeviceLocalDataSource = deviceLocalDataSource;
    }

    public static DeviceRepository getInstance(DeviceRemoteDataSource deviceRemoteDataSource,
                                               DeviceLocalDataSource deviceLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (DeviceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceRepository(deviceRemoteDataSource, deviceLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Observer getDoorInfo(String deviceCode, String doorCode, OnResponseListener<DoorInfo> listener) {
        return mDeviceRemoteDataSource.getDoorInfo(deviceCode, doorCode, listener);
    }

    @Override
    public Observer getDoorInfos(DoorInfoReq doorInfoReq, OnResponseListener<List<DoorInfo>> listener) {
        return mDeviceRemoteDataSource.getDoorInfos(doorInfoReq, listener);
    }

    @Override
    public Observer getAllDoorInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<List<DoorInfo>> listener) {
        return mDeviceRemoteDataSource.getAllDoorInfo(deviceInfoReq, listener);
    }

    @Override
    public Observer getDeviceInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<DeviceInfo> listener) {
        return mDeviceRemoteDataSource.getDeviceInfo(deviceInfoReq, listener);
    }

    @Override
    public Observer getDeviceInfos(List<String> deviceCodes, OnResponseListener<List<DeviceInfo>> listener) {
        return mDeviceRemoteDataSource.getDeviceInfos(deviceCodes, listener);
    }

    @Override
    public Observer editDeviceInfo(UpdateDeviceInfo updateDeviceInfo, OnResponseListener listener) {
        return mDeviceRemoteDataSource.editDeviceInfo(updateDeviceInfo, listener);
    }

    @Override
    public Observer editLinenInDoor(LinenInDoorInfoReq linenInDoorInfoReq, OnResponseListener<List<LinenInDoorInfo>> listener) {
        return mDeviceRemoteDataSource.editLinenInDoor(linenInDoorInfoReq, listener);
    }

    @Override
    public Observer getLinenInfo(String epcCode, OnResponseListener<LinenInfo> listener) {
        return mDeviceRemoteDataSource.getLinenInfo(epcCode, listener);
    }

    @Override
    public Observer getLinenInfo(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener) {
        return mDeviceRemoteDataSource.getLinenInfo(epcCodes, listener);
    }

    @Override
    public Observer linenStatistics(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener) {
        return mDeviceRemoteDataSource.linenStatistics(epcCodes, listener);
    }

    @Override
    public Observer linenCirculation(LinenOperationRecordsReq linenOperationRecordsReq, OnResponseListener listener) {
        return mDeviceRemoteDataSource.linenCirculation(linenOperationRecordsReq, listener);
    }

    @Override
    public Observer checkVer(AppVersionReq versionReq, OnResponseListener<AppVersionResp> listener) {
        return mDeviceRemoteDataSource.checkVer(versionReq, listener);
    }

    @Override
    public Observer keepAlive(DeviceInfoReq deviceInfoReq, OnResponseListener<HeartbeatInfo> listener) {
        return mDeviceRemoteDataSource.keepAlive(deviceInfoReq, listener);
    }

    @Override
    public DeviceInfo getLocalDeviceInfo() {
        return mDeviceLocalDataSource.getLocalDeviceInfo();
    }
}
