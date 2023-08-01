package com.sheltersdog.volunte.repository

import com.sheltersdog.volunte.entity.Volunteer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class VolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
){

    fun save(entity: Volunteer): Mono<Volunteer> {
        return reactiveMongoTemplate.save(entity)
    }
}