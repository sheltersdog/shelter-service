package com.sheltersdog.shelter.entity

import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.user.entity.User

data class ShelterJoinUser(
    val name: String? = null,
    val nickname: String,
    val authorities: List<ShelterAuthority> = listOf(),
    val userId: String,
    val email: String,
    val user: User? = null,
    val profileImageUrl: String,
    val status: Boolean = true,
)
