package com.sheltersdog.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "properties.kakao")
data class KakaoProperties(
    val appKey: String,
    val apiKey: String,
    val webKey: String,
    val adminKey: String,
)
