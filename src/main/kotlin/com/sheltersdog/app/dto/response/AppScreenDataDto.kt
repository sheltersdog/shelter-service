package com.sheltersdog.app.dto.response

import com.sheltersdog.app.model.DataType
import com.sheltersdog.app.model.PlatformType

data class AppScreenDataDto(
    val id: String,
    val page: String,
    val title: String,
    val content: String,
    val platformTypes: List<PlatformType>,
    val dataType: DataType,
)