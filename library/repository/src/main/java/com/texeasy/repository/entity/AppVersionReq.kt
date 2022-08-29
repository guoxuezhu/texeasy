package com.texeasy.repository.entity

/**
 * 版本检测接口(请求体)
 */
data class AppVersionReq(var appCode: String, var version: String)