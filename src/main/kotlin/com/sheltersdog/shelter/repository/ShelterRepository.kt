package com.sheltersdog.shelter.repository

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.address.entity.Address
import com.sheltersdog.core.database.updateQuery
import com.sheltersdog.core.database.whereRegionCode
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import kotlin.reflect.KProperty

@Repository
class ShelterRepository @Autowired constructor(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    suspend fun save(shelter: Shelter): Shelter {
        return reactiveMongoTemplate.save(shelter).awaitSingle()
    }

    suspend fun findById(id: String): Shelter? {
        return reactiveMongoTemplate.findById(id, Shelter::class.java).awaitSingleOrNull()
    }

    suspend fun updateById(
        id: String,
        updateFields: Map<KProperty<*>, Any?>,
    ): UpdateResult {
        val update = updateQuery(updateFields)

        return reactiveMongoTemplate.updateFirst(
            Query.query(
                where(Shelter::id).`is`(id)
            ), update, Shelter::class.java
        ).awaitSingle()
    }

    suspend fun getShelterList(
        keyword: String = "",
        regionCode: Long = 0,
        isVolunteerRecruiting: Boolean? = null,
        isDonationPossible: Boolean? = null,
        pageable: Pageable = PageRequest.of(
            0, 10, Sort.by(Sort.Direction.DESC, "id")
        ),
        statuses: List<ShelterStatus> = listOf(ShelterStatus.ACTIVE),
        loadAddresses: Boolean = false,
    ): List<Shelter> {
        val query = Query().with(pageable)

        if (keyword.isNotEmpty()) query.addCriteria(
            where(Shelter::searchKeyword).regex(".*$keyword+")
        )

        if (isVolunteerRecruiting != null) query.addCriteria(
            where(Shelter::isVolunteerRecruiting).`is`(isVolunteerRecruiting)
        )

        if (isDonationPossible != null) query.addCriteria(
            where(Shelter::isDonationPossible).`is`(isDonationPossible)
        )

        query.whereRegionCode(
            regionCode = regionCode,
            key = Shelter::regionCode,
        )

        val entities = reactiveMongoTemplate.find(
            query.addCriteria(
                where(Shelter::status).`in`(statuses)
            ), Shelter::class.java
        ).asFlow().toList()

        if (loadAddresses) {
            return this.loadingAddresses(entities)
        }
        return entities
    }

    suspend fun loadingAddresses(shelters: List<Shelter>): List<Shelter> {
        val regionCodes = shelters.map { it.regionCode }
        val addresses = reactiveMongoTemplate.find(
            Query.query(
                where(Address::regionCd).`in`(regionCodes)
            ), Address::class.java
        ).asFlow().toList()

        val addressMap = addresses.associateBy { it.regionCd }
        return shelters.map { shelter ->
            shelter.copy(address = addressMap[shelter.regionCode])
        }
    }

}
