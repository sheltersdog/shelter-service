package com.sheltersdog.volunteer.dto.request

import com.sheltersdog.volunteer.entity.model.SourceType
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range

data class PutVolunteer(
    val id: String,
    val isShort: Boolean = true,

    @field:NotEmpty
    val categories: List<String> = listOf(),
    val startDate: String? = null,
    val endDate: String? = null,

    val days: List<String> = listOf(),

    val sourceType: SourceType = SourceType.SERVICE,

    val startTime: String? = null,
    val endTime: String? = null,

    val isAlwaysRecruiting: Boolean = false,

    @field:NotNull
    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00)
    val regionCode: Long,
    val detailAddress: String? = null,

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00)
    val arriveRegionCode: Long? = null,
    val arriveDetailAddress: String? = null,

    val isPrivateDetailAddress: Boolean = true,

    val content: String? = null,
    val url: String? = null,

    val exposeStartDate: String? = null,
    val exposeEndDate: String? = null,
)
