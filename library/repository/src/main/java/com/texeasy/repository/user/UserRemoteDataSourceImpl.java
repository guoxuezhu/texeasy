package com.texeasy.repository.user;

import com.example.common.http.HttpDisposableObserver;
import com.example.common.http.OnResponseListener;
import com.example.common.utils.RxUtils;
import com.texeasy.repository.entity.CardAttestationReq;
import com.texeasy.repository.entity.CardBindReq;
import com.texeasy.repository.entity.FaceAttestationReq;
import com.texeasy.repository.entity.FingerAttestationReq;
import com.texeasy.repository.entity.OperationRecordsReq;
import com.texeasy.repository.entity.PswAttestationReq;
import com.texeasy.repository.entity.QrAttestationReq;
import com.texeasy.repository.entity.UserAttestationResponse;

import java.util.List;

import io.reactivex.Observer;

public class UserRemoteDataSourceImpl implements UserRemoteDataSource {
    private UserApiService apiService;
    private volatile static UserRemoteDataSourceImpl INSTANCE = null;

    public static UserRemoteDataSourceImpl getInstance(UserApiService apiService) {
        if (INSTANCE == null) {
            synchronized (UserRemoteDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRemoteDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private UserRemoteDataSourceImpl(UserApiService apiService) {
        this.apiService = apiService;
    }


    @Override
    public Observer userAttestationByCard(CardAttestationReq cardAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return apiService.userAttestationByCard(cardAttestationReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userAttestationByQR(QrAttestationReq qrAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return apiService.userAttestationByQR(qrAttestationReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userAttestationByFinger(FingerAttestationReq fingerAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return apiService.userAttestationByFinger(fingerAttestationReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userAttestationByFace(FaceAttestationReq faceAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return apiService.userAttestationByFace(faceAttestationReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userAttestationByPassword(PswAttestationReq pswAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return apiService.userAttestationByPassword(pswAttestationReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userLogout(OnResponseListener listener) {
        return apiService.userLogout()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer addOperationRecord(OperationRecordsReq operationRecordsReq, OnResponseListener listener) {
        return apiService.addOperationRecord(operationRecordsReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer batchAddOperationRecord(List<OperationRecordsReq> operationRecordsReqs, OnResponseListener listener) {
        return apiService.batchAddOperationRecord(operationRecordsReqs)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userBindIcCard(CardBindReq cardBindReq, OnResponseListener listener) {
        return apiService.userBindIcCard(cardBindReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }

    @Override
    public Observer userTwoBindIcCard(CardBindReq cardBindReq, OnResponseListener listener) {
        return apiService.userTwoBindIcCard(cardBindReq)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(new HttpDisposableObserver<>(listener));
    }
}
