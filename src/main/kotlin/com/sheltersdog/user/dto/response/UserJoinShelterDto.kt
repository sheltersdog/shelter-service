package com.sheltersdog.user.dto.response

import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.model.ShelterAuthority

data class UserJoinShelterDto (
    val userId: String,
    val shelterId: String,
    val userName: String,
    val userEmail: String,
    val shelterName: String,

    val authorities: List<ShelterAuthority> = listOf(),

    val shelter: ShelterDto? = null,
)
