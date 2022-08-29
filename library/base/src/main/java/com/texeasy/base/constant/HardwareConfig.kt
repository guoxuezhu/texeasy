package com.texeasy.base.constant

class HardwareConfig {
    companion object {
        /**
         * 心跳配置
         */
        val HEART_BEAT = listOf(5, 10, 15, 20)

        /**
         * 进入设置界面登录帐号
         */
        const val SETTING_ACCOUNT = "admin"

        /**
         * 进入设置界面登录密码
         */
        const val SETTING_PSW = "123456"

        /**
         * 所有门
         */
        const val ALL_DOORS = "all"

        /**
         * RFID扫描时间(s)
         */
        var rfidScanTime = 5

        /**
         * RFID扫描时间(s)，不限时间
         */
        var rfidScanUnlimitedTime = Long.MAX_VALUE

        /**
         * 柜门检测时间周期(s)
         */
        var doorCheckTimeInterval = 1;
    }
}