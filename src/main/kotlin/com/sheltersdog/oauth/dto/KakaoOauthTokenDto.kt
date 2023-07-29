package com.sheltersdog.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoOauthTokenDto(
    @field:JsonProperty(value = "access_token")
    val accessToken: String = "",
    @field:JsonProperty(value = "token_type")
    val tokenType: String = "",
    @field:JsonProperty(value = "refresh_token")
    val refreshToken: String = "",
    @field:JsonProperty(value = "expires_in")
    val expiresIn: Long = 0,
    @field:JsonProperty(value = "refresh_token_expires_in")
    val refreshTokenExpiresIn: Long = 0,
) {
}