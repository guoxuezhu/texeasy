package com.texeasy.repository.entity

/**
 * 用户操作记录(请求报文)
 */
data class OperationRecordsReq(
    //用户编码（冗余字段，可以通过临时令牌获取用户信息）
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
