package com.sheltersdog.app.dto.request

import com.sheltersdog.app.model.PlatformType

data class GetAppScreenDataRequest(
    val page: String,
    val platformType: PlatformType,
)
