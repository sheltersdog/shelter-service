package com.sheltersdog.image.entity

import com.sheltersdog.image.entity.model.ImageStatus
import com.sheltersdog.image.entity.model.ImageType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Image(
    @Id
    val id: ObjectId? = null,
    val type: ImageType,
    val filename: String = "",
    val resizeFilename: String? = null,

    val status: ImageStatus = ImageStatus.NOT_CONNECTED,
    val width: Int = 0,
    val height: Int = 0,
    val size: Long = 0,

    val url: String = "",
    val regDate: LocalDateTime = LocalDateTime.now(),
)