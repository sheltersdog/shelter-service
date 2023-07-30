package com.sheltersdog.user.entity

import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterAuthority

data class UserJoinShelter(
    val userId: String,
    val shelterId: String,
    val name: String = "",
    val authorities: List<ShelterAuthority> = listOf(
        ShelterAuthority.GALLERY_VIEW
    ),

    var shelter: Shelter? = null,
)
