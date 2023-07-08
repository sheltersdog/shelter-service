package com.sheltersdog.core.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "properties.jwt")
data class JwtProperties(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiredTime: Long,
    val refreshTokenExpiredTime: Long,
    val issuer: String,
    val subject: String,
)