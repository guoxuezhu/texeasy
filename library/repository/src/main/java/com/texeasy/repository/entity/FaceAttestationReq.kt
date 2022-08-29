package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 人脸认证(请求体)
 */
data class FaceAttestationReq(
    //人脸识别图片
    var img: String,
    //设备编码
    var deviceCode: String,
    //是否为首页调用 【1：首页调用 0:配送员登录处调用 2:综合柜登录调用】
    var isHome: String = if ("comprehensive" == BaseApplication.getFlavor()) "2" else "1",
    //机构id
    var orgId: String? = BaseApplication.getOrgId()
)
