package com.texeasy.repository.user;

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

public interface UserRemoteDataSource {
    /**
     * 用户卡号认证接口
     *
     * @param cardAttestationReq
     * @return
     */
    Observer userAttestationByCard(CardAttestationReq cardAttestationReq, OnResponseListener<UserAttestationResponse> listener);

    /**
     * 用户二维码认证接口
     *
     * @param qrAttestationReq
     * @return
     */
    Observer userAttestationByQR(QrAttestationReq qrAttestationReq, OnResponseListener<UserAttestationResponse> listener);

    /**
     * 用户指纹认证接口
     *
     * @param fingerAttestationReq
     * @return
     */
    Observer userAttestationByFinger(FingerAttestationReq fingerAttestationReq, OnResponseListener<UserAttestationResponse> listener);

    /**
     * 用户人脸证接口
     *
     * @param faceAttestationReq
     * @return
     */
    Observer userAttestationByFace(FaceAttestationReq faceAttestationReq, OnResponseListener<UserAttestationResponse> listener);

    /**
     * 用户账号密码认证接口
     *
     * @param pswAttestationReq
     * @return
     */
    Observer userAttestationByPassword(PswAttestationReq pswAttestationReq, OnResponseListener<UserAttestationResponse> listener);

    /**
     * 用户退出接口
     *
     * @return
     */
    Observer userLogout(OnResponseListener listener);

    /**
     * 添加用户操作记录接口
     *
     * @param operationRecordsReq
     * @return
     */
    Observer addOperationRecord(OperationRecordsReq operationRecordsReq, OnResponseListener listener);

    /**
     * 批量添加用户操作记录接口
     *
     * @param operationRecordsReqs
     * @return
     */
    Observer batchAddOperationRecord(List<OperationRecordsReq> operationRecordsReqs, OnResponseListener listener);

    /**
     * 绑定IC卡接口设计
     *
     * @param cardBindReq
     * @return
     */
    Observer userBindIcCard(CardBindReq cardBindReq, OnResponseListener listener);

    /**
     * 二次确认绑定IC卡接口设计
     *
     * @param cardBindReq
     * @return
     */
    Observer userTwoBindIcCard(CardBindReq cardBindReq, OnResponseListener listener);
}
