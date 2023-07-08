package com.sheltersdog.app.dto.request

import com.sheltersdog.app.model.DataType
import com.sheltersdog.app.model.PlatformType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class PostAppScreenDataRequest(
    @field:NotBlank val page: String,
    @field:NotEmpty val platformTypes: List<PlatformType>,
    @field:NotEmpty val dataType: DataType,
    @field:NotBlank val title: String,
    @field:NotBlank val content: String,
)
