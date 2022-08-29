package com.texeasy.repository.entity

/**
 * 布草流转接口(请求报文)
 */
data class LinenOperationRecordsReq(
    //布草芯片编码列表
    var epcCodes: List<String>,
    //用户编码
    var userCode: String,
    //设备编号
    var deviceCode: String,
    //柜门编号
    var doorCode: String,
    //操作类型: 【1：布草上柜；2：布草领用；3：布草归还；4：布草回收】
    var operationType: Int,
    //操作时间
    var operationDate: String
)
