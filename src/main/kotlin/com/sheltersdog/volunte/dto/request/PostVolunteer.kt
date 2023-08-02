package com.sheltersdog.volunte.dto.request

import com.sheltersdog.volunte.entity.model.SourceType
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range

data class PostVolunteer(
    val sourceType: SourceType = SourceType.SERVICE,

    val shelterName: String? = null,
    val shelterId: String? = null,

    val isShort: Boolean = true,
    @field:NotEmpty
    val categories: List<String> = listOf(),

    @field:NotNull
    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00)
    val regionCode: Long,
    val detailAddress: String? = null,
    val isPrivateDetailAddress: Boolean = true,

    val isAlwaysRecruiting: Boolean = false,
    val startDate: String? = null,
    val endDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val days: List<String> = listOf(),

    val content: String? = null,
    val url: String? = null,

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00)
    val arriveRegionCode: Long? = null,
    val arriveDetailAddress: String? = null,
)
