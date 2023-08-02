package com.sheltersdog.volunte.repository

import com.sheltersdog.address.entity.Address
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.volunte.entity.CrawlingVolunteer
import com.sheltersdog.volunte.entity.Volunteer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Repository
class VolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun save(entity: Volunteer): Mono<Volunteer> {
        return reactiveMongoTemplate.save(entity)
    }

    fun getVolunteers(
        keyword: String = "",
        regionCode: Long = 0,
        date: String = "",
        categories: List<String> = listOf(),
        pageable: Pageable = Pageable.unpaged(),
        loadAddresses: Boolean = false,
    ): Mono<List<Volunteer>> {
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
            .collectList()

        if (loadAddresses) {
            return entities.flatMap (this::loadingAddresses)
        }
        return entities
    }

    fun loadingAddresses(volunteers: List<Volunteer>): Mono<List<Volunteer>> {
        val regionCodes = volunteers.map { it.regionCode }.toList()

        return reactiveMongoTemplate.find(
            Query.query(where(Address::regionCd).`in`(regionCodes)), Address::class.java
        ).collectList().map { addresses ->
            val addressMap = addresses.associateBy { it.regionCd }
            return@map volunteers.map { volunteer ->
                volunteer.copy(address = addressMap[volunteer.regionCode])
            }.toList()
        }.switchIfEmpty { Mono.just(volunteers) }
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