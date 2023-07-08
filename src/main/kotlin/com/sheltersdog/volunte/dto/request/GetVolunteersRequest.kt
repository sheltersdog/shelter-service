package com.sheltersdog.volunte.dto.request

data class GetVolunteersRequest(
    val page: Int = 0,
    val size: Int = 10,
    val keyword: String = "",
    val addressRegionCode: Long = 0,
    val category: String = "",
)
