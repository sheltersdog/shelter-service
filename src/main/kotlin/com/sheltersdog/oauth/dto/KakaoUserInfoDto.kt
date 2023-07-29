package com.sheltersdog.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoDto(
    @field:JsonProperty(value = "id")
    val id: Long = 0,
    @field:JsonProperty(value = "connected_at")
    val connectedAt: String = "",
)
