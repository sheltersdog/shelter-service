package com.sheltersdog.user.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class UserHistory(
    @Id
    val id: ObjectId? = null,
    val regDate: LocalDateTime = LocalDateTime.now(),
    val type: String,
)
