package com.texeasy.repository.entity

/**
 * 柜门绑定用户信息
 */
data class UserInfo(
    //用户编号
    var userCode: String,
    //用户名称
    var userName: String,
    //用户角色
    var RoleName: String,
    //用户状态【0：有效；1：无效/离职】
    var userStatus: Int
)
