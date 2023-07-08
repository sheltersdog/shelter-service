package com.sheltersdog.volunte.dto.response

import com.sheltersdog.address.dto.AddressDto

data class VolunteeerDto(
    val id: String,
    val name: String,
    val shelterName: String,
    val isShort: Boolean,
    val categories: List<String>,
    val address: AddressDto? = null,
    val detailAddress: String = "",
    val startDate: String,
    val endDate: String,
    val day: String = "",
    val time: String = "",
    val content: String = "",
    val url: String = "",
)
