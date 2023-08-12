package com.sheltersdog.foreverdog.dto.request

import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus

data class GetForeverdogsRequest(
    val size: Int = 10,
    val page: Int = 0,
    val keyword: String? = null,
    val shelterId: String = "",
    val statuses: List<String> = listOf(
        ForeverdogStatus.SHELTER_PROTECTION.name
    ),
)
