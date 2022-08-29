package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 设备信息（请求体）
 */
data class DeviceInfoReq(
    var deviceCode: String,
    var orgId: String? = BaseApplication.getOrgId() //机构id
)
