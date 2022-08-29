package com.texeasy.repository.user;

import com.example.common.http.BaseResponse;
import com.texeasy.repository.entity.CardAttestationReq;
import com.texeasy.repository.entity.CardBindReq;
import com.texeasy.repository.entity.FaceAttestationReq;
import com.texeasy.repository.entity.FingerAttestationReq;
import com.texeasy.repository.entity.OperationRecordsReq;
import com.texeasy.repository.entity.PswAttestationReq;
import com.texeasy.repository.entity.QrAttestationReq;
import com.texeasy.repository.entity.UserAttestationResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    /**
     * 用户卡号认证接口
     *
     * @param cardAttestationReq
     * @return
     */
    @POST("userAttestationByCard")
    Observable<BaseResponse<UserAttestationResponse>> userAttestationByCard(@Body CardAttestationReq cardAttestationReq);

    /**
     * 用户二维码认证接口
     *
     * @param qrAttestationReq
     * @return
     */
    @POST("userAttestationByQR")
    Observable<BaseResponse<UserAttestationResponse>> userAttestationByQR(@Body QrAttestationReq qrAttestationReq);

    /**
     * 用户指纹认证接口
     *
     * @param fingerAttestationReq
     * @return
     */
    @POST("userAttestationByFinger")
    Observable<BaseResponse<UserAttestationResponse>> userAttestationByFinger(@Body FingerAttestationReq fingerAttestationReq);

    /**
     * 用户人脸认证接口
     *
     * @param faceAttestationReq
     * @return
     */
    @POST("userAttestationByFace")
    Observable<BaseResponse<UserAttestationResponse>> userAttestationByFace(@Body FaceAttestationReq faceAttestationReq);

    /**
     * 用户账号密码认证接口
     *
     * @param pswAttestationReq
     * @return
     */
    @POST("userAttestationByPassword")
    Observable<BaseResponse<UserAttestationResponse>> userAttestationByPassword(@Body PswAttestationReq pswAttestationReq);

    /**
     * 用户退出接口
     *
     * @return
     */
    @POST("userLogout")
    Observable<BaseResponse> userLogout();

    /**
     * 添加用户操作记录接口
     *
     * @param operationRecordsReq
     * @return
     */
    @POST("addOperationRecord")
    Observable<BaseResponse> addOperationRecord(@Body OperationRecordsReq operationRecordsReq);

    /**
     * 批量添加用户操作记录接口
     *
     * @param operationRecordsReqs
     * @return
     */
    @POST("batchAddOperationRecord")
    Observable<BaseResponse> batchAddOperationRecord(@Body List<OperationRecordsReq> operationRecordsReqs);

    /**
     * 绑定IC卡接口设计
     *
     * @param cardBindReq
     * @return
     */
    @POST("userBindIcCard")
    Observable<BaseResponse> userBindIcCard(@Body CardBindReq cardBindReq);

    /**
     * 二次确认绑定IC卡接口设计
     *
     * @param cardBindReq
     * @return
     */
    @POST("userTwoBindIcCard")
    Observable<BaseResponse> userTwoBindIcCard(@Body CardBindReq cardBindReq);

}
