package com.sheltersdog.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "properties.cors")
data class CorsProperties(
    val allowedOrigins: List<String>,
)
