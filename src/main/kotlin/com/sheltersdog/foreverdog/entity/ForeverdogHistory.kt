package com.sheltersdog.foreverdog.entity

import java.time.LocalDate

data class ForeverdogHistory(
    val history: String,
    val createdDate: LocalDate = LocalDate.now(),
)
