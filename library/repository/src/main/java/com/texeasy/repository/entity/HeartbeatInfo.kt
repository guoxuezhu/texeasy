package com.texeasy.repository.entity

/**
 * 心跳
 */
data class HeartbeatInfo(
    //心跳间隔时长（单位：秒）
    var keepAliveTime: Int,
    //语音提醒功能【0：开启；1：关闭】
    var voiceReminder: Int,
    //设备自测功能【0：开启；1：关闭】
    var selfTest: Int
) {
    var isOpenVoiceReminder: Boolean = voiceReminder == 0
    var isOpenSelfTest: Boolean = selfTest == 0
}
