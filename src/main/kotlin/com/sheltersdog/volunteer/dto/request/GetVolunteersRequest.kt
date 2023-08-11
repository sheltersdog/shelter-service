package com.sheltersdog.volunteer.dto.request

import com.sheltersdog.core.model.SheltersdogStatus

data class GetVolunteersRequest(
    val page: Int = 0,
    val size: Int = 10,
    val keyword: String = "",
    val regionCode: Long = 0,
    val date: String = "",
    val categories: List<String> = listOf(),
    val statuses: List<String> = listOf(SheltersdogStatus.ACTIVE.name),
)
