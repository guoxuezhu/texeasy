package com.texeasy.repository.cabinetlist;

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

public interface DeviceRemoteDataSource {
    /**
     * 柜门信息查询接口
     *
     * @param deviceCode
     * @param doorCode
     * @return
     */
    Observer getDoorInfo(String deviceCode, String doorCode, OnResponseListener<DoorInfo> listener);

    /**
     * 柜门信息批量查询接口
     *
     * @param doorInfoReq
     * @return
     */
    Observer getDoorInfos(DoorInfoReq doorInfoReq, OnResponseListener<List<DoorInfo>> listener);

    /**
     * 所有柜门信息批量查询接口
     *
     * @param deviceInfoReq
     * @return
     */
    Observer getAllDoorInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<List<DoorInfo>> listener);

    /**
     * 设备信息查询接口
     *
     * @param deviceInfoReq
     * @return
     */
    Observer getDeviceInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<DeviceInfo> listener);

    /**
     * 设备信息批量查询接口
     *
     * @param deviceCodes
     * @return
     */
    Observer getDeviceInfos(List<String> deviceCodes, OnResponseListener<List<DeviceInfo>> listener);

    /**
     * 设备信息更新接口
     *
     * @param updateDeviceInfo
     * @return
     */
    Observer editDeviceInfo(UpdateDeviceInfo updateDeviceInfo, OnResponseListener listener);

    /**
     * 柜内布草变更接口
     *
     * @param linenInDoorInfoReq
     * @return
     */
    Observer editLinenInDoor(LinenInDoorInfoReq linenInDoorInfoReq, OnResponseListener<List<LinenInDoorInfo>> listener);

    /**
     * 布草查询接口
     *
     * @param epcCode
     * @return
     */
    Observer getLinenInfo(String epcCode, OnResponseListener<LinenInfo> listener);

    /**
     * 布草批量查询接口
     *
     * @param epcCodes
     * @return
     */
    Observer getLinenInfo(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener);

    /**
     * 布草统计查询接口
     *
     * @param epcCodes
     * @return
     */
    Observer linenStatistics(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener);

    /**
     * 布草流转接口
     *
     * @param linenOperationRecordsReq
     * @return
     */
    Observer linenCirculation(LinenOperationRecordsReq linenOperationRecordsReq, OnResponseListener listener);

    /**
     * 版本检测接口
     *
     * @param versionReq
     * @return
     */
    Observer checkVer(AppVersionReq versionReq, OnResponseListener<AppVersionResp> listener);

    /**
     * 心跳接口
     *
     * @param deviceInfoReq
     * @return
     */
    Observer keepAlive(DeviceInfoReq deviceInfoReq, OnResponseListener<HeartbeatInfo> listener);
}
