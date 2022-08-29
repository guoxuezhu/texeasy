package com.texeasy.repository.entity

/**
 * 设备信息
 */
data class DeviceInfo(
    //设备编码
    var deviceCode: String = "1",
    //设备名称
    var deviceName: String = "",
    //设备类型【1：科室发衣柜；2：综合发放柜；3：收脏柜】待定
    var deviceType: Int = 0,
    //所属部门
    var departmentName: String = "",
    //设备状态【1：正常；2：停用；3：异常】
    var deviceStatus: Int = 0,
    //当前数量
    var currentStorage: Int = 0,
    //存储总量
    var totalStorage: Int = 0,
    //柜门数
    var doorCount: Int = 0,
    //柜组数（主副柜管理联数量）
    var groupCount: Int = 0,
    //软件版本号
    var version: String = "",
    //机构id
    var orgId: String = "",
    //设备所在位置
    var pos: String = "",
    //所属医院
    var hospital: String = "",
    //app_ID
    var appId: String = "",
    //SDK密匙
    var sdkKey: String = "",
    //激活码
    var activeKey: String = "",
    //当前存量比
    var stockRate: String = ""
)