package com.texeasy.base.entity

/**
 * 设备信息
 */
data class DeviceSettingInfo(
    var heartBeat: Int = 0,
    var isOpenVoice: Boolean = false,
    var isSelfCheck: Boolean = false,
    var isDebug: Boolean = false,
    var camera90: Boolean = false,
    var comName: String = "/dev/ttyS4",
    var rfidScanTime: Int = 5
)