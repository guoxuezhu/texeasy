package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 用户账号密码认证(请求体)
 */
data class PswAttestationReq(
    //用户账号
    var userName: String,
    //密码
    var password: String,
    //设备编码
    var deviceCode: String,
    //是否为首页调用 【1：首页调用 0:配送员登录处调用 2:综合柜登录调用】
    var isHome: String = if ("comprehensive" == BaseApplication.getFlavor()) "2" else "1"
)
