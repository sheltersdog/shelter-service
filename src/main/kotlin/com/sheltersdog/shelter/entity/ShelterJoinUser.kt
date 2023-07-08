package com.sheltersdog.shelter.entity

import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.user.entity.User
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ShelterJoinUser(
    @Id
    val id: ObjectId? = null,
    val name: String = "",
    val nickname: String = "",
    val authorities: List<ShelterAuthority> = listOf(
        ShelterAuthority.VOLUNTEER,
        ShelterAuthority.GALLERY_VIEW
    ),
    @DBRef(lazy = true)
    val user: User,
)
