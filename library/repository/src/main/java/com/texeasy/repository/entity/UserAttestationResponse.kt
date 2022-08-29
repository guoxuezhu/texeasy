package com.texeasy.repository.entity

/**
 * 认证响应报文
 */
data class UserAttestationResponse(
    //临时令牌
    var authToken: String,
    //用户信息
    var userMessage: UserMessage
)
