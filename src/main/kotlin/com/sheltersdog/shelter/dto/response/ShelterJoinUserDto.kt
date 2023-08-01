package com.sheltersdog.shelter.dto.response

import com.sheltersdog.shelter.entity.model.ShelterAuthority

data class ShelterJoinUserDto(
    val name: String,
    val nickname: String,
    val authorities: List<ShelterAuthority>,
    val userId: String,
    val email: String,
    val status: Boolean = true,
)
