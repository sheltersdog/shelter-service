package com.sheltersdog.core.database

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.data.mongodb")
data class MongoProperties(
    val database: String,
    val username: String,
    val password: String,
    val uri: String,
)
