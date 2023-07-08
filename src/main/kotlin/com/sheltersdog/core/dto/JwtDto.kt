package com.sheltersdog.core.dto

data class JwtDto(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiredTime: Long,
    val refreshTokenExpiredTime: Long,
)