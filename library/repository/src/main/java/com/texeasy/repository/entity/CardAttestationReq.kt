package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 用户卡号认证(请求体)
 */
data class CardAttestationReq(
    //用户卡号
    var cardNo: String,
    //设备编码
    var deviceCode: String,
    //是否为首页调用 【1：首页调用 0:配送员登录处调用 2:综合柜登录调用】
    var isHome: String = if ("comprehensive" == BaseApplication.getFlavor()) "2" else "1",
    //机构id
    var orgId: String? = BaseApplication.getOrgId()
)
