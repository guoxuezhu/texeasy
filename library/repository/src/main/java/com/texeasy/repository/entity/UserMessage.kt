package com.texeasy.repository.entity

/**
 * 用户信息
 */
data class UserMessage(
    val id: Int,
    //用户名称
    var userName: String,
    //用户编码
    var userCode: String,
    //角色名称
    var roleName: String,
    //角色编码
    var roleCode: String,
    //可以打开的柜门门锁编号列表
    var doorCodes: MutableList<String>
)
