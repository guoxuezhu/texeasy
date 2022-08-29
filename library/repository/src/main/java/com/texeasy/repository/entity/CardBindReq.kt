package com.texeasy.repository.entity

/**
 * 绑定IC卡(请求报文)
 */
data class CardBindReq(
    //用户账号
    var userName: String?,
    //密码
    var password: String?,
    //设备编码
    var deviceCode: String?,
    //用户卡号
    var cardNo: String?
)
