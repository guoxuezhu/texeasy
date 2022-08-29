package com.texeasy.base.entity

/**
 * 用户登录信息
 */
data class BasicSettingInfo(
    var serverIp: String = "",
    var serverPort: String = "",
    var deviceCode: String = "",
    var socketKey: String = ""
)