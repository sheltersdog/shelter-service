package com.sheltersdog.foreverdog.dto.request

data class GetForeverdogsRequest(
    val size: Int = 10,
    val page: Int = 0,
    val keyword: String? = null,
    val shelterId: String = "",
)
