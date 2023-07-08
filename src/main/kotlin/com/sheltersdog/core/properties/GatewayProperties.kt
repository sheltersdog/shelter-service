package com.sheltersdog.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "properties.gateway")
data class GatewayProperties(
    val key: String,
    val value: String,
)
