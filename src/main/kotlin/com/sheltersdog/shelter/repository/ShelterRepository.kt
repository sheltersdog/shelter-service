package com.sheltersdog.shelter.repository

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.shelter.entity.Shelter
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository

@Repository
class ShelterRepository @Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun save(shelter: Shelter): Shelter {
        return reactiveMongoTemplate.save(shelter).awaitSingle()
    }

    suspend fun findById(id: String): Shelter? {
        return reactiveMongoTemplate.findById(id, Shelter::class.java).awaitSingleOrNull()
    }

    suspend fun updateVolunteerCount(
        id: String,
        volunteerActiveCount: Int,
        volunteerTotalCount: Int,
    ): UpdateResult {
        return reactiveMongoTemplate.updateFirst(
            Query.query(
                where(Shelter::id).`is`(id)
            ),
            Update().set(Shelter::volunteerActiveCount.name, volunteerActiveCount)
                .set(Shelter::volunteerTotalCount.name, volunteerTotalCount),
            Shelter::class.java
        ).awaitSingle()
    }

}