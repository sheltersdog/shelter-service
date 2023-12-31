package com.sheltersdog.shelter.dto.response

import com.sheltersdog.shelter.entity.model.ShelterAuthority

data class ShelterJoinUserDto(
    val name: String? = null,
    val nickname: String,
    val authorities: List<ShelterAuthority>,
    val userId: String,
    val email: String,
    val profileImageUrl: String,
    val status: Boolean = true,
)
