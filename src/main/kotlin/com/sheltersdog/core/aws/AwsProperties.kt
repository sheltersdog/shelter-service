package com.sheltersdog.core.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "properties.aws")
data class AwsProperties(
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val endPoint: String,
    val bucket: String,
)