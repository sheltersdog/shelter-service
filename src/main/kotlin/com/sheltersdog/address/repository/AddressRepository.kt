package com.sheltersdog.address.repository

import com.sheltersdog.address.entity.Address
import com.sheltersdog.address.model.AddressType
import com.sheltersdog.address.model.getParentPropertyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class AddressRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    suspend fun getAddresses(
        type: AddressType,
        parentCode: String,
        keyword: String): Address {

        val query = Query.query(where(Address::type).`is`(type))
        if (parentCode.isBlank() && keyword.isBlank()) {
            return reactiveMongoTemplate.find(
                query, Address::class.java
            ).awaitSingle()
        }

        val parentProperty = type.getParentPropertyCode()

        if (parentCode.isNotBlank()) {
            query.addCriteria(where(parentProperty).`is`(parentCode))
        }

        if (keyword.isNotBlank()) {
            query.addCriteria(
                where(Address::regionName).regex(".*$keyword+")
            )
        }

        return reactiveMongoTemplate.find(
            query,
            Address::class.java,
        ).awaitSingle()
    }

    suspend fun saveAll(addresses: List<Address>): Flow<Address> {
        return reactiveMongoTemplate.insertAll(addresses).asFlow()
    }

    suspend fun getAddressById(id: String): Address {
        return reactiveMongoTemplate.findById(id, Address::class.java)
            .awaitSingle()
    }

    suspend fun getAddressByRegionCode(regionCode: Long): Address {
        return reactiveMongoTemplate.findOne(
            Query.query(where(Address::regionCd).`is`(regionCode)),
            Address::class.java
        ).awaitSingle()
    }
}