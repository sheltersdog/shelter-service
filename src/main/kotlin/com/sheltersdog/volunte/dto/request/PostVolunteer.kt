package com.sheltersdog.volunte.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Range

data class PostVolunteer(
    @field:NotBlank val shelterId: String,
    val isShort: Boolean = true,
    @field:NotEmpty val categories: List<String> = listOf(),

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00) val regionCode: Long,
    val detailAddress: String = "",

    val alwaysRecruit: Boolean = false,
    @field:NotBlank val startDate: String,
    @field:NotBlank val endDate: String,
    @field:NotBlank val startTime: String,
    @field:NotBlank val endTime: String,
    @field:NotBlank val days: List<String> = listOf(),

    val content: String? = null,

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00) val arriveRegionCode: Long = 0,
    val arriveDetailAddress: String? = null,
)
