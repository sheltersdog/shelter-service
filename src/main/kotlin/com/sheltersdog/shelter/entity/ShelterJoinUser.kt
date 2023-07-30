package com.sheltersdog.shelter.entity

import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.user.entity.User

data class ShelterJoinUser(
    val name: String = "",
    val nickname: String = "",
    val authorities: List<ShelterAuthority> = listOf(
        ShelterAuthority.GALLERY_VIEW
    ),
    val userId: String = "",
    val user: User? = null,
    val status: Boolean,
)
