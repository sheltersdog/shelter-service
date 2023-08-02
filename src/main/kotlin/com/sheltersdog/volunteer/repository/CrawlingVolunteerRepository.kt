package com.sheltersdog.volunteer.repository

import com.sheltersdog.address.entity.Address
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.volunteer.entity.CrawlingVolunteer
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
class CrawlingVolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun getCrawlingVolunteers(
        keyword: String = "",
        regionCode: Long = 0,
        date: String = "",
        categories: List<String> = listOf(),
        pageable: Pageable = Pageable.unpaged(),
        loadAddresses: Boolean = false,
    ): Mono<List<CrawlingVolunteer>> {
        val query = Query().with(pageable)

        if (keyword.isNotEmpty()) query.addCriteria(
            where(CrawlingVolunteer::searchKeyword).regex(".*$keyword+")
        )

        if (categories.isNotEmpty()) query.addCriteria(
            where(CrawlingVolunteer::categories).`in`(categories)
        )

        betweenStartDateAndDateDateQuery(date, query)
        whereRegionCodeQuery(regionCode, query)

        val entities = reactiveMongoTemplate.find(query, CrawlingVolunteer::class.java)
            .collectList()

        if (loadAddresses) {
            return entities.flatMap(this::loadingAddresses)
        }

        return entities
    }

    fun saveCrawlingVolunteer(entity: CrawlingVolunteer): Mono<CrawlingVolunteer> {
        return reactiveMongoTemplate.save(entity)
    }

    fun loadingAddresses(volunteers: List<CrawlingVolunteer>): Mono<List<CrawlingVolunteer>> {
        val regionCodes = volunteers.map { it.addressRegionCode }.toList()

        return reactiveMongoTemplate.find(
            Query.query(where(Address::regionCd).`in`(regionCodes)), Address::class.java
        ).collectList().map { addresses ->
            val addressMap = addresses.associateBy { it.regionCd }
            volunteers.forEach { volunteer -> volunteer.address = addressMap[volunteer.addressRegionCode] }
            return@map volunteers
        }.switchIfEmpty { Mono.just(volunteers) }
    }

    fun getVolunteerCategories(regionCode: Long, date: String): Mono<Array<String>> {
        val query: Query = Query()

        if (regionCode != 0L) {
            whereRegionCodeQuery(regionCode, query)
        }

        if (date.isNotBlank()) {
            betweenStartDateAndDateDateQuery(date, query)
        }

        return reactiveMongoTemplate.find(
            query, CrawlingVolunteer::class.java
        ).collectList().map { entities ->
            val categories = mutableSetOf<String>()
            entities.forEach { entity -> entity.categories.forEach(categories::add) }
            categories.toTypedArray()
        }
    }

    private fun betweenStartDateAndDateDateQuery(dateString: String, query: Query) {
        if (dateString.isBlank()) return

        val date = yyyyMMddToLocalDate(dateString)
        query.addCriteria(
            Criteria().orOperator(
                where(CrawlingVolunteer::startDate).lte(date)
                    .and(CrawlingVolunteer::endDate).gte(date),
                where(CrawlingVolunteer::startDate).lte(date)
                    .and(CrawlingVolunteer::endDate).`is`(null),
                where(CrawlingVolunteer::startDate).`is`(null)
                    .and(CrawlingVolunteer::endDate).gte(date)
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
                where(CrawlingVolunteer::addressRegionCode)
                    .gte(umdCode).lt(umdCode + 100)
            )
        } else if (sggCode != sidoCode) {
            query.addCriteria(
                where(CrawlingVolunteer::addressRegionCode)
                    .gte(sggCode).lt(sggCode + 1000_00)
            )
        } else {
            query.addCriteria(
                where(CrawlingVolunteer::addressRegionCode)
                    .gte(sidoCode).lt(sidoCode + 1000_000_00)
            )
        }
    }

}