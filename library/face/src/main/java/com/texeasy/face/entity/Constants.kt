package com.texeasy.face.entity

/**
 * 方式一： 填好APP_ID等参数，进入激活界面激活
 */
const val APP_ID = "4fhTgiqGoXhvtqTYHifJBwtPpAncxM6NwiSQdY7rpMC1"
const val SDK_KEY = "J8sDKNjykngfazN31LHUSLNRuKeR4JZpA2cJrpee8Kyb"
const val ACTIVE_KEY = "85F1-11A9-W13L-X6UQ"

/**
 * 方式二： 在激活界面读取本地配置文件进行激活
 *
 * 配置文件名称，格式如下：
 * APP_ID:XXXXXXXXXXXXX
 * SDK_KEY:XXXXXXXXXXXXXXX
 * ACTIVE_KEY:XXXX-XXXX-XXXX-XXXX
 */
const val ACTIVE_CONFIG_FILE_NAME = "activeConfig.txt"
const val APP_ROOT_PATH = "Texeasy"