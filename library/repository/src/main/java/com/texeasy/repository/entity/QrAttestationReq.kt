package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 用户二维码认证(请求体)
 */
data class QrAttestationReq(
    //二维码号
    var qrCodeNo: String,
    //设备编码
    var deviceCode: String,
    //是否为首页调用 【1：首页调用 0:配送员登录处调用 2:综合柜登录调用】
    var isHome: String = if ("comprehensive" == BaseApplication.getFlavor()) "2" else "1"
)
