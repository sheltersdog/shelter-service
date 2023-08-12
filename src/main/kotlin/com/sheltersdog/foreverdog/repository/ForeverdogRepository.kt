package com.sheltersdog.foreverdog.repository

import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.shelter.entity.Shelter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component

@Component
class ForeverdogRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    suspend fun save(entity: Foreverdog): Foreverdog {
        return reactiveMongoTemplate.save(entity).awaitSingle()
    }

    suspend fun findById(foreverdogId: String): Foreverdog? {
        return reactiveMongoTemplate.findById(foreverdogId, Foreverdog::class.java).awaitSingleOrNull()
    }

    suspend fun getForeverdogs(
        keyword: String = "",
        shelterId: String = "",
        statuses: List<ForeverdogStatus> = listOf(
            ForeverdogStatus.SHELTER_PROTECTION
        ),
        pageable: Pageable = Pageable.unpaged(),
        isContainShelter: Boolean = false,
    ): List<Foreverdog> {
        val query = Query().with(pageable).addCriteria(
            where(Foreverdog::status).`in`(statuses)
        )

        if (shelterId.isNotEmpty()) {
            where(Foreverdog::shelterId).`is`(shelterId)
        }

        if (keyword.isNotEmpty()) query.addCriteria(
            where(Foreverdog::searchKeyword).regex(".*$keyword+")
        )

        val entities = reactiveMongoTemplate.find(query, Foreverdog::class.java)
            .asFlow().toList()

        if (isContainShelter) {
            return this.loadingShelters(entities)
        }

        return entities
    }

    suspend fun loadingShelters(foreverdogs: List<Foreverdog>): List<Foreverdog> {
        val shelterIds = foreverdogs.map { it.shelterId }.toList()

        val shelters = reactiveMongoTemplate.find(
            Query.query(where(Shelter::id).`in`(shelterIds)), Shelter::class.java
        ).asFlow().toList()

        val shelterMap = shelters.associateBy { it.id.toString() }
        return foreverdogs.map { foreverdog ->
            foreverdog.copy(shelter = shelterMap[foreverdog.shelterId])
        }.toList()
    }

}