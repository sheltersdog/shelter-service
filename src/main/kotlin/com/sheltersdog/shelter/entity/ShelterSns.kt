package com.sheltersdog.shelter.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class ShelterSns(
    val site: String,
    val url: String,
)
