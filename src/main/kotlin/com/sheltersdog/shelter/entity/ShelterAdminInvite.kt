package com.sheltersdog.shelter.entity

import com.sheltersdog.shelter.entity.model.ShelterAuthority
import java.time.LocalDateTime

data class ShelterAdminInvite(
    val email: String,
    val authorities: List<ShelterAuthority>,
    val status: Boolean = false,
    val regDate: LocalDateTime = LocalDateTime.now(),
    val expiredDate: LocalDateTime = regDate.plusDays(1)
)
