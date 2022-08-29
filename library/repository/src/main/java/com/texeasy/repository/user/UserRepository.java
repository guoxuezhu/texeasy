package com.texeasy.repository.user;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.common.base.BaseApplication;
import com.example.common.base.BaseModel;
import com.example.common.http.OnResponseListener;
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

/**
 * 用户仓库
 *
 * @author Zhihu
 */
public class UserRepository extends BaseModel implements UserRemoteDataSource {
    private volatile static UserRepository INSTANCE = null;
    private final UserRemoteDataSource mUserRemoteDataSource;

    private UserRepository(@NonNull UserRemoteDataSource userRemoteDataSource) {
        this.mUserRemoteDataSource = userRemoteDataSource;
    }

    public static UserRepository getInstance(UserRemoteDataSource userRemoteDataSource) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(userRemoteDataSource);
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
    public Observer userAttestationByCard(CardAttestationReq cardAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return mUserRemoteDataSource.userAttestationByCard(cardAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                if (data != null && data.getUserMessage() != null) {
                    BaseApplication.setCurrentUserId(data.getUserMessage().getId());
                }
                listener.onSuccess(data);
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                listener.onError(rspcode, rspmsg);
            }
        });
    }

    @Override
    public Observer userAttestationByQR(QrAttestationReq qrAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return mUserRemoteDataSource.userAttestationByQR(qrAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                if (data != null && data.getUserMessage() != null) {
                    BaseApplication.setCurrentUserId(data.getUserMessage().getId());
                }
                listener.onSuccess(data);
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                listener.onError(rspcode, rspmsg);
            }
        });
    }

    @Override
    public Observer userAttestationByFinger(FingerAttestationReq fingerAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return mUserRemoteDataSource.userAttestationByFinger(fingerAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                if (data != null && data.getUserMessage() != null) {
                    BaseApplication.setCurrentUserId(data.getUserMessage().getId());
                }
                listener.onSuccess(data);
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                listener.onError(rspcode, rspmsg);
            }
        });
    }

    @Override
    public Observer userAttestationByFace(FaceAttestationReq faceAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return mUserRemoteDataSource.userAttestationByFace(faceAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                if (data != null && data.getUserMessage() != null) {
                    BaseApplication.setCurrentUserId(data.getUserMessage().getId());
                }
                listener.onSuccess(data);
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                listener.onError(rspcode, rspmsg);
            }
        });
    }

    @Override
    public Observer userAttestationByPassword(PswAttestationReq pswAttestationReq, OnResponseListener<UserAttestationResponse> listener) {
        return mUserRemoteDataSource.userAttestationByPassword(pswAttestationReq, new OnResponseListener<UserAttestationResponse>() {
            @Override
            public void onSuccess(UserAttestationResponse data) {
                if (data != null && data.getUserMessage() != null) {
                    BaseApplication.setCurrentUserId(data.getUserMessage().getId());
                }
                listener.onSuccess(data);
            }

            @Override
            public void onError(String rspcode, String rspmsg) {
                listener.onError(rspcode, rspmsg);
            }
        });
    }

    @Override
    public Observer userLogout(OnResponseListener listener) {
        return mUserRemoteDataSource.userLogout(listener);
    }

    @Override
    public Observer addOperationRecord(OperationRecordsReq operationRecordsReq, OnResponseListener listener) {
        return mUserRemoteDataSource.addOperationRecord(operationRecordsReq, listener);
    }

    @Override
    public Observer batchAddOperationRecord(List<OperationRecordsReq> operationRecordsReqs, OnResponseListener listener) {
        return mUserRemoteDataSource.batchAddOperationRecord(operationRecordsReqs, listener);
    }

    @Override
    public Observer userBindIcCard(CardBindReq cardBindReq, OnResponseListener listener) {
        return mUserRemoteDataSource.userBindIcCard(cardBindReq, listener);
    }

    @Override
    public Observer userTwoBindIcCard(CardBindReq cardBindReq, OnResponseListener listener) {
        return mUserRemoteDataSource.userTwoBindIcCard(cardBindReq, listener);
    }
}
