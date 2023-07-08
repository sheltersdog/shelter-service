package com.sheltersdog.shelter.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class ShelterSns(
    @Id
    val id: ObjectId? = null,
    val site: String,
    val url: String,
    val introduce: String? = null,
)
