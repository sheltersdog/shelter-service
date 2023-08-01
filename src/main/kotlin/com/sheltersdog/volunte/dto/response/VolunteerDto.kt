package com.sheltersdog.volunte.dto.response

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.shelter.dto.response.ShelterDto

data class VolunteerDto(
    val id: String,
    val shelterName: String,
    val isShort: Boolean,
    val categories: List<String>,
    val address: AddressDto? = null,
    val detailAddress: String? = null,
    val isPrivateAddress: Boolean = true,

    val isAlwaysRecruiting: Boolean = false,
    val startDate: String? = null,
    val endDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val days: List<String> = listOf(),
    val content: String? = null,
    val url: String? = null,
    val shelterId: String? = null,
    val shelter: ShelterDto? = null,
)
