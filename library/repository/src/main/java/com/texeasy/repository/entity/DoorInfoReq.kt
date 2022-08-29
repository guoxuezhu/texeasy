package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 柜门信息（请求体）
 */
data class DoorInfoReq(
    var deviceCode: String,
    var doorCodes: List<String>,
    //机构id
    var orgId: String? = BaseApplication.getOrgId()
)
