package com.texeasy.repository.entity

/**
 * 布草信息
 */
data class LinenInfo(
    //布草编号
    var linenCode: String,
    //布草名称
    var linenName: String,
    //布草数量
    var linenCount: Int,
    //布草尺寸 待定
    var linenSize: String,
    //---------------------//
    //布草种类
    var linenTypeName: String,
    //使用类型【1：通用；2：专用】
    var useType: Int,
    //使用者编码（用户编码）
    var userCode: String,
    //使用者（用户）
    var userName: String,
    //布草使用情况【1：正常；2：滞留；3：丢失；4：报废】
    var useStatus: Int,
    //布草芯片编码
    var epcCode: String,
    //流转时间
    var circulationDate: String,
    //滞留时长(滞留多少天)
    var retentionTime: Int

)
