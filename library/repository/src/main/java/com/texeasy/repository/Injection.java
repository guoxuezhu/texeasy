package com.texeasy.repository;


import com.texeasy.repository.utils.RetrofitClient;
import com.texeasy.repository.cabinetlist.DeviceApiService;
import com.texeasy.repository.cabinetlist.DeviceLocalDataSource;
import com.texeasy.repository.cabinetlist.DeviceLocalDataSourceImpl;
import com.texeasy.repository.cabinetlist.DeviceRemoteDataSource;
import com.texeasy.repository.cabinetlist.DeviceRemoteDataSourceImpl;
import com.texeasy.repository.cabinetlist.DeviceRepository;
import com.texeasy.repository.user.UserApiService;
import com.texeasy.repository.user.UserRemoteDataSource;
import com.texeasy.repository.user.UserRemoteDataSourceImpl;
import com.texeasy.repository.user.UserRepository;

/**
 * 注入全局的数据仓库，可以考虑使用Dagger2
 */
public class Injection {
    public static DeviceRepository provideCabinetRepository() {
        //网络API服务
        DeviceApiService apiService = RetrofitClient.getInstance().create(DeviceApiService.class);
        //网络数据源
        DeviceRemoteDataSource remoteDataSource = DeviceRemoteDataSourceImpl.getInstance(apiService);
        //本地数据源
        DeviceLocalDataSource localDataSource = DeviceLocalDataSourceImpl.getInstance();
        //两条分支组成一个数据仓库
        return DeviceRepository.getInstance(remoteDataSource, localDataSource);
    }

    public static UserRepository provideUserRepository() {
        //网络API服务
        UserApiService apiService = RetrofitClient.getInstance().create(UserApiService.class);
        //网络数据源
        UserRemoteDataSource remoteDataSource = UserRemoteDataSourceImpl.getInstance(apiService);
        //数据仓库
        return UserRepository.getInstance(remoteDataSource);
    }
}
