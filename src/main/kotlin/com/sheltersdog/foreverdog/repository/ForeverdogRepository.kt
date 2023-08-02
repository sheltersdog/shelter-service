package com.sheltersdog.foreverdog.repository

import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.shelter.entity.Shelter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class ForeverdogRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    fun save(entity: Foreverdog): Mono<Foreverdog> {
        return reactiveMongoTemplate.save(entity)
    }

    fun getForeverdogs(
        keyword: String = "",
        shelterId: String = "",
        pageable: Pageable = Pageable.unpaged(),
        isContainShelter: Boolean = false,
    ): Mono<List<Foreverdog>> {
        val query = Query().with(pageable)

        if (shelterId.isNotEmpty()) {
            where(Foreverdog::shelterId).`is`(shelterId)
        }

        if (keyword.isNotEmpty()) query.addCriteria(
            where(Foreverdog::searchKeyword).regex(".*$keyword+")
        )

        val entities = reactiveMongoTemplate.find(query, Foreverdog::class.java)
            .collectList()

        if (isContainShelter) {
            return entities.flatMap(this::loadingShelters)
        }

        return entities
    }

    fun loadingShelters(foreverdogs: List<Foreverdog>): Mono<List<Foreverdog>> {
        val shelterIds = foreverdogs.map { it.shelterId }.toList()

        return reactiveMongoTemplate.find(
            Query.query(where(Shelter::id).`in`(shelterIds)), Shelter::class.java
        ).collectList().map { shelters ->
            val shelterMap = shelters.associateBy { it.id.toString() }
            return@map foreverdogs.map { foreverdog ->
                foreverdog.copy(shelter = shelterMap[foreverdog.shelterId])
            }.toList()
        }.switchIfEmpty { Mono.just(foreverdogs) }
    }

}