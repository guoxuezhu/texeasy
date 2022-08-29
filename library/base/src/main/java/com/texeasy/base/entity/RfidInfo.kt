package com.texeasy.base.entity

/**
 * rfid信息（门和天线关系）
 */
data class RfidInfo(
    var door: String = "",
    var antenna: String = "",
    var power: String = ""
)