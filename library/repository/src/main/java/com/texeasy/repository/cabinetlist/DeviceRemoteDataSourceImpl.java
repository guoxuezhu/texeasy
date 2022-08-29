package com.texeasy.repository.cabinetlist;

import com.example.common.http.HttpDisposableObserver;
import com.example.common.http.OnResponseListener;
import com.example.common.utils.RxUtils;
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

public class DeviceRemoteDataSourceImpl implements DeviceRemoteDataSource {
    private DeviceApiService apiService;
    private volatile static DeviceRemoteDataSourceImpl INSTANCE = null;

    public static DeviceRemoteDataSourceImpl getInstance(DeviceApiService apiService) {
        if (INSTANCE == null) {
            synchronized (DeviceRemoteDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceRemoteDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private DeviceRemoteDataSourceImpl(DeviceApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observer getDoorInfo(String deviceCode, String doorCode, OnResponseListener<DoorInfo> listener) {
        return apiService.getDoorInfo(deviceCode, doorCode)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getDoorInfos(DoorInfoReq doorInfoReq, OnResponseListener<List<DoorInfo>> listener) {
        return apiService.getDoorInfos(doorInfoReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getAllDoorInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<List<DoorInfo>> listener) {
        return apiService.getAllDoorInfo(deviceInfoReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getDeviceInfo(DeviceInfoReq deviceInfoReq, OnResponseListener<DeviceInfo> listener) {
        return apiService.getDeviceInfo(deviceInfoReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getDeviceInfos(List<String> deviceCodes, OnResponseListener<List<DeviceInfo>> listener) {
        return apiService.getDeviceInfos(deviceCodes)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer editDeviceInfo(UpdateDeviceInfo updateDeviceInfo, OnResponseListener listener) {
        return apiService.editDeviceInfo(updateDeviceInfo)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer editLinenInDoor(LinenInDoorInfoReq linenInDoorInfoReq, OnResponseListener<List<LinenInDoorInfo>> listener) {
        return apiService.editLinenInDoor(linenInDoorInfoReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getLinenInfo(String epcCode, OnResponseListener<LinenInfo> listener) {
        return apiService.getLinenInfo(epcCode)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer getLinenInfo(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener) {
        return apiService.getLinenInfo(epcCodes)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer linenStatistics(List<String> epcCodes, OnResponseListener<List<LinenInfo>> listener) {
        return apiService.linenStatistics(epcCodes)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer linenCirculation(LinenOperationRecordsReq linenOperationRecordsReq, OnResponseListener listener) {
        return apiService.linenCirculation(linenOperationRecordsReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer checkVer(AppVersionReq versionReq, OnResponseListener<AppVersionResp> listener) {
        return apiService.checkVer(versionReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer keepAlive(DeviceInfoReq deviceInfoReq, OnResponseListener<HeartbeatInfo> listener) {
        return apiService.keepAlive(deviceInfoReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

}
