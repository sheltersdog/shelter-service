package com.sheltersdog.foreverdog.repository

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.core.database.updateQuery
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
import kotlin.reflect.KProperty

@Component
class ForeverdogRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    suspend fun save(entity: Foreverdog): Foreverdog {
        return reactiveMongoTemplate.save(entity).awaitSingle()
    }

    suspend fun findById(
        foreverdogId: String,
        isContainShelter: Boolean = false,
    ): Foreverdog? {
        val foreverdog = reactiveMongoTemplate
            .findById(foreverdogId, Foreverdog::class.java)
            .awaitSingleOrNull()
            ?: return null
        if (!isContainShelter) return foreverdog

        val shelter = reactiveMongoTemplate
            .findById(foreverdog.shelterId, Shelter::class.java)
            .awaitSingleOrNull()
            ?: return foreverdog

        return foreverdog.copy(shelter = shelter)
    }

    suspend fun updateById(
        id: String,
        updateFields: Map<KProperty<*>, Any?>,
    ): UpdateResult {
        val update = updateQuery(updateFields)

        return reactiveMongoTemplate.updateFirst(
            Query.query(
                where(Foreverdog::id).`is`(id)
            ), update, Foreverdog::class.java
        ).awaitSingle()
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