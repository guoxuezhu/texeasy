package com.texeasy.repository.entity

import com.example.common.base.BaseApplication

/**
 * 布草流转接口(请求报文)
 */
data class LinenInDoorInfoReq(
    //设备编号
    var deviceCode: String,
    //布草芯片编码列表
    var epcCodes: List<String>,
    //机构id
    var orgId: String? = BaseApplication.getOrgId(),
    //用户id
    var id: Int = BaseApplication.getCurrentUserId()
)
