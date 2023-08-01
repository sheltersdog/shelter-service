package com.sheltersdog.shelter.repository

import com.sheltersdog.shelter.entity.Shelter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class ShelterRepository @Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    fun save(shelter: Shelter): Mono<Shelter> {
        return reactiveMongoTemplate.save(shelter)
    }

    fun findById(id: String): Mono<Shelter> {
        return reactiveMongoTemplate.findById(id, Shelter::class.java)
    }


}