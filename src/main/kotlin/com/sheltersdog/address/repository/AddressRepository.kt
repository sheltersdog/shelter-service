package com.sheltersdog.address.repository

import com.sheltersdog.address.entity.Address
import com.sheltersdog.address.model.AddressType
import com.sheltersdog.address.model.getParentPropertyCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class AddressRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun getAddresses(
        type: AddressType,
        parentCode: String,
        keyword: String): Flux<Address> {

        val query = Query.query(where(Address::type).`is`(type))
        if (parentCode.isBlank() && keyword.isBlank()) {
            return reactiveMongoTemplate.find(
                query, Address::class.java
            )
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
        )
    }

    fun saveAll(addresses: List<Address>) {
        reactiveMongoTemplate.insertAll(addresses).subscribe()
    }

    fun getAddressById(id: String): Mono<Address> {
        return reactiveMongoTemplate.findById(id, Address::class.java)
    }

    fun getAddressByRegionCode(regionCode: Long): Mono<Address> {
        return reactiveMongoTemplate.findOne(
            Query.query(where(Address::regionCd).`is`(regionCode)),
            Address::class.java
        )
    }
}