package com.sheltersdog.volunteer.repository

import com.sheltersdog.address.entity.Address
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

@Repository
class VolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    suspend fun save(entity: Volunteer): Volunteer {
        return reactiveMongoTemplate.save(entity).awaitSingle()
    }

    suspend fun findById(id: String): Volunteer? {
        return reactiveMongoTemplate.findById(id, Volunteer::class.java).awaitSingleOrNull()
    }

    suspend fun getVolunteers(
        keyword: String = "",
        regionCode: Long = 0,
        date: String = "",
        categories: List<String> = listOf(),
        pageable: Pageable = Pageable.unpaged(),
        loadAddresses: Boolean = false,
    ): List<Volunteer> {
        val query = Query().with(pageable)

        if (keyword.isNotEmpty()) query.addCriteria(
            where(Volunteer::searchKeyword).regex(".*$keyword+")
        )

        if (categories.isNotEmpty()) query.addCriteria(
            where(Volunteer::categories).`in`(categories)
        )

        betweenStartDateAndDateDateQuery(date, query)
        whereRegionCodeQuery(regionCode, query)

        val entities = reactiveMongoTemplate.find(query, Volunteer::class.java)
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

    private fun whereRegionCodeQuery(regionCode: Long, query: Query) {
        if (regionCode == 0L) return

        val sidoCode = (regionCode / 1000_000_00) * 1000_000_00
        val sggCode = (regionCode / 1000_00) * 1000_00
        val umdCode = (regionCode / 100) * 100

        if (umdCode != sggCode) {
            query.addCriteria(
                where(Volunteer::regionCode)
                    .gte(umdCode).lt(umdCode + 100)
            )
        } else if (sggCode != sidoCode) {
            query.addCriteria(
                where(Volunteer::regionCode)
                    .gte(sggCode).lt(sggCode + 1000_00)
            )
        } else {
            query.addCriteria(
                where(Volunteer::regionCode)
                    .gte(sidoCode).lt(sidoCode + 1000_000_00)
            )
        }
    }
}