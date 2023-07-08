package com.sheltersdog.shelter.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.awt.Image

@Document
data class Shelter(
    @Id
    val id: ObjectId? = null,
    val name: String,
    val introduce: String,
    val shelterSns: List<ShelterSns>,

    val address: String,
    val x: Double? = null,
    val y: Double? = null,

    val profileImageUrl: String = "",

    @DBRef(lazy = true)
    val joinUsers: List<ShelterJoinUser> = listOf(),
    @DBRef(lazy = true)
    val images: List<Image> = listOf(),

    // https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template
    @Version
    val version: Long,
)