package com.sheltersdog.app.repository

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.entity.AppScreenData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AppRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun getScreenData(requestBody: GetAppScreenDataRequest): Flux<AppScreenData> {
        return reactiveMongoTemplate.find(
            Query.query(
                where(AppScreenData::platformTypes).`in`(requestBody.platformType)
            ).addCriteria(
                where(AppScreenData::page).`is`(requestBody.page)
            ),
            AppScreenData::class.java
        )
    }

    fun postScreenData(entity: AppScreenData): Mono<AppScreenData> {
        return reactiveMongoTemplate.save(entity)
    }

}