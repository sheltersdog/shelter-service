package com.sheltersdog.volunte.repository

import com.sheltersdog.address.entity.Address
import com.sheltersdog.volunte.entity.CrawlingVolunteer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Repository
class CrawlingVolunteerRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun getCrawlingVolunteers(
        keyword: String,
        addressRegionCode: Long,
        categories: List<String>,
        pageable: Pageable
    ): Mono<List<CrawlingVolunteer>> {
        val query: Query = Query().with(pageable)

        if (keyword.isNotEmpty()) query.addCriteria(
            where(CrawlingVolunteer::searchKeyword).regex(".*$keyword+")
        )

        if (addressRegionCode != 0L) {
            val sidoCode = (addressRegionCode / 1000_000_00) * 1000_000_00
            val sggCode = (addressRegionCode / 1000_00) * 1000_00
            val umdCode = (addressRegionCode / 100) * 100

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

        if (categories.isNotEmpty()) query.addCriteria(
            where(CrawlingVolunteer::categories).`in`(categories)
        )

        return reactiveMongoTemplate.find(query, CrawlingVolunteer::class.java)
            .collectList().flatMap(this::loadingAddresses)
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

}