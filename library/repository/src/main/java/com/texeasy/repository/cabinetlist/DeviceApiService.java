package com.texeasy.repository.cabinetlist;

import com.example.common.http.BaseResponse;
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

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceApiService {
    /**
     * 柜门信息查询接口
     *
     * @param deviceCode
     * @param doorCode
     * @return
     */
    @POST("doorInfo")
    Observable<BaseResponse<DoorInfo>> getDoorInfo(@Body String deviceCode, @Body String doorCode);

    /**
     * 柜门信息批量查询接口
     *
     * @param doorInfoReq
     * @return
     */
    @POST("doorInfos")
    Observable<BaseResponse<List<DoorInfo>>> getDoorInfos(@Body DoorInfoReq doorInfoReq);

    /**
     * 所有柜门信息批量查询接口
     *
     * @param deviceInfoReq
     * @return
     */
    @POST("allDoorInfo")
    Observable<BaseResponse<List<DoorInfo>>> getAllDoorInfo(@Body DeviceInfoReq deviceInfoReq);

    /**
     * 设备信息查询接口
     *
     * @param deviceInfoReq
     * @return
     */
    @POST("deviceInfo")
    Observable<BaseResponse<DeviceInfo>> getDeviceInfo(@Body DeviceInfoReq deviceInfoReq);

    /**
     * 设备信息批量查询接口
     *
     * @param deviceCodes
     * @return
     */
    @POST("deviceInfos")
    Observable<BaseResponse<List<DeviceInfo>>> getDeviceInfos(@Body List<String> deviceCodes);

    /**
     * 设备信息更新接口
     *
     * @param updateDeviceInfo
     * @return
     */
    @POST("editDeviceInfo")
    Observable<BaseResponse> editDeviceInfo(@Body UpdateDeviceInfo updateDeviceInfo);

    /**
     * 柜内布草变更接口
     *
     * @param linenInDoorInfoReq
     * @return
     */
    @POST("editLinenInDoor")
    Observable<BaseResponse<List<LinenInDoorInfo>>> editLinenInDoor(@Body LinenInDoorInfoReq linenInDoorInfoReq);

    /**
     * 布草查询接口
     *
     * @param epcCode
     * @return
     */
    @POST("linenInfo")
    Observable<BaseResponse<LinenInfo>> getLinenInfo(@Body String epcCode);

    /**
     * 布草批量查询接口
     *
     * @param epcCodes
     * @return
     */
    @POST("linenInfos")
    Observable<BaseResponse<List<LinenInfo>>> getLinenInfo(@Body List<String> epcCodes);

    /**
     * 布草统计查询接口
     *
     * @param epcCodes
     * @return
     */
    @POST("linenStatistics")
    Observable<BaseResponse<List<LinenInfo>>> linenStatistics(@Body List<String> epcCodes);

    /**
     * 布草流转接口
     *
     * @param linenOperationRecordsReq
     * @return
     */
    @POST("linenCirculation")
    Observable<BaseResponse> linenCirculation(@Body LinenOperationRecordsReq linenOperationRecordsReq);

    /**
     * 版本检测接口
     *
     * @param versionReq
     * @return
     */
    @POST("checkVer")
    Observable<BaseResponse<AppVersionResp>> checkVer(@Body AppVersionReq versionReq);

    /**
     * 心跳接口
     *
     * @param deviceInfoReq
     * @return
     */
    @POST("keepAlive")
    Observable<BaseResponse<HeartbeatInfo>> keepAlive(@Body DeviceInfoReq deviceInfoReq);


}
