package com.sheltersdog.volunte.dto.request

data class GetVolunteersRequest(
    val page: Int = 0,
    val size: Int = 10,
    val keyword: String = "",
    val regionCode: Long = 0,
    val date: String = "",
    val categories: List<String> = listOf(),
)
