package com.sheltersdog.app.entity

import com.sheltersdog.app.model.DataType
import com.sheltersdog.app.model.PlatformType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class AppScreenData(
    @Id
    val id: ObjectId? = null,
    val page: String,
    val title: String,
    val content: String,
    val platformTypes: List<PlatformType>,
    val dataType: DataType,
)