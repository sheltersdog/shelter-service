package com.sheltersdog.volunte.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Range

data class PostCrawlingVolunteer(
    @field:NotBlank val name: String,
    @field:NotBlank val shelterName: String,
    val isShort: Boolean = true,
    @field:NotEmpty val categories: List<String> = listOf(),

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00) val addressRegionCode: Long,

    val detailAddress: String = "",
    @field:NotBlank val startDate: String,
    @field:NotBlank val endDate: String,
    @field:NotBlank val day: String = "",
    val time: String = "",
    val content: String = "",
    @field:NotBlank val url: String,
)
