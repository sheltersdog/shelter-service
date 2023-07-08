package com.sheltersdog.user.entity

import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import org.springframework.data.mongodb.core.mapping.DBRef

data class UserJoinShelter(
    val name: String = "",
    val authorities: List<ShelterAuthority> = listOf(
        ShelterAuthority.VOLUNTEER,
        ShelterAuthority.GALLERY_VIEW
    ),

    @DBRef(lazy = true)
    val shelter: Shelter,
)
