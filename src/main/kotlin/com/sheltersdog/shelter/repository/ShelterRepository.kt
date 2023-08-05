package com.sheltersdog.shelter.repository

import com.sheltersdog.shelter.entity.Shelter
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ShelterRepository @Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun save(shelter: Shelter): Shelter {
        return reactiveMongoTemplate.save(shelter).awaitSingle()
    }

    suspend fun findById(id: String): Shelter? {
        return reactiveMongoTemplate.findById(id, Shelter::class.java).awaitSingle()
    }

}