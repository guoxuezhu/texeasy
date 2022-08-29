package com.texeasy.repository.entity

/**
 * 柜门信息
 */
class DoorInfo(
    //柜门编码
    var doorCode: String = "",
    //柜内布草信息
    var linenInfo: LinenInfo? = null,
    //柜门绑定用户信息
    var userInfo: UserInfo? = null
)
