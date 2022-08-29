package com.texeasy.repository.entity

/**
 * 综合柜布草变更信息
 */
data class LinenInDoorInfo(
    //序号
    var no: Int = 0,
    //布草名称
    var linenName: String = "",
    //布草种类
    var linenTypeName: String = "",
    //布草尺寸 待定
    var linenSize: String = "",
    //目前柜内布草数量
    var currentLinenCount: Int = 0,
    //新增数量/存放数量
    var addNum: Int = 0,
    //减少数量/领用数量
    var minusNum: Int = 0
)