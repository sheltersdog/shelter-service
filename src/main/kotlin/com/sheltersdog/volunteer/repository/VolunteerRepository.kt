package com.sheltersdog.volunteer.repository

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.address.entity.Address
import com.sheltersdog.core.database.updateQuery
import com.sheltersdog.core.database.whereRegionCode
import com.sheltersdog.core.model.SheltersdogStatus
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.volunteer.entity.Volunteer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import kotlin.reflect.KProperty

@Repository
class VolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate,
) {

    suspend fun save(entity: Volunteer): Volunteer {
        return reactiveMongoTemplate.save(entity).awaitSingle()
    }

    suspend fun findById(id: String): Volunteer? {
        return reactiveMongoTemplate.findById(id, Volunteer::class.java).awaitSingleOrNull()
    }

    suspend fun updateById(
        id: String,
        updateFields: Map<KProperty<*>, Any?>,
    ): UpdateResult {
        val update = updateQuery(updateFields)

        return reactiveMongoTemplate.updateFirst(
            Query.query(
                where(Volunteer::id).`is`(id)
            ), update, Volunteer::class.java
        ).awaitSingle()
    }

    suspend fun updateAllByShelterId(
        shelterId: String,
        updateFields: Map<KProperty<*>, Any?>,
    ): UpdateResult {
        val update = updateQuery(updateFields)

        return reactiveMongoTemplate.updateMulti(
            Query.query(
                where(Volunteer::shelterId).`is`(shelterId).andOperator(
                    where(Volunteer::status).`is`(SheltersdogStatus.ACTIVE)
                )
            ), update, Volunteer::class.java
        ).awaitSingle()
    }

    suspend fun getVolunteers(
        keyword: String = "",
        regionCode: Long = 0,
        date: String = "",
        categories: List<String> = listOf(),
        pageable: Pageable = Pageable.unpaged(),
        loadAddresses: Boolean = false,
        statuses: List<SheltersdogStatus> = listOf(SheltersdogStatus.ACTIVE),
    ): List<Volunteer> {
        val query = Query().with(pageable)

        if (keyword.isNotEmpty()) query.addCriteria(
            where(Volunteer::searchKeyword).regex(".*$keyword+")
        )

        if (categories.isNotEmpty()) query.addCriteria(
            where(Volunteer::categories).`in`(categories)
        )

        betweenStartDateAndDateDateQuery(date, query)
        query.whereRegionCode(
            regionCode = regionCode,
            key = Volunteer::regionCode,
        )

        val entities = reactiveMongoTemplate.find(
            query.addCriteria(
                where(Volunteer::status).`in`(statuses)
            ), Volunteer::class.java
        )
            .asFlow().toList()

        if (loadAddresses) {
            return this.loadingAddresses(entities)
        }
        return entities
    }

    suspend fun loadingAddresses(volunteers: List<Volunteer>): List<Volunteer> {
        val regionCodes = volunteers.map { it.regionCode }.toList()

        val addresses = reactiveMongoTemplate.find(
            Query.query(where(Address::regionCd).`in`(regionCodes)), Address::class.java
        ).asFlow().toList()

        val addressMap = addresses.associateBy { it.regionCd }
        return volunteers.map { volunteer ->
            volunteer.copy(address = addressMap[volunteer.regionCode])
        }.toList()
    }

    private fun betweenStartDateAndDateDateQuery(dateString: String, query: Query) {
        if (dateString.isBlank()) return

        val date = yyyyMMddToLocalDate(dateString)
        query.addCriteria(
            Criteria().orOperator(
                where(Volunteer::startDate).lte(date)
                    .and(Volunteer::endDate).gte(date),
                where(Volunteer::startDate).lte(date)
                    .and(Volunteer::endDate).`is`(null),
                where(Volunteer::startDate).`is`(null)
                    .and(Volunteer::endDate).gte(date)
            )
        )
    }
}