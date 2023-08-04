package com.sheltersdog.app.repository

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.entity.AppScreenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository

@Repository
class AppRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    suspend fun getScreenData(requestBody: GetAppScreenDataRequest): Flow<AppScreenData> {
        return reactiveMongoTemplate.find(
            Query.query(
                where(AppScreenData::platformTypes).`in`(requestBody.platformType)
            ).addCriteria(
                where(AppScreenData::page).`is`(requestBody.page)
            ),
            AppScreenData::class.java
        ).asFlow()
    }

    suspend fun postScreenData(entity: AppScreenData): AppScreenData {
        return reactiveMongoTemplate.save(entity).awaitSingle()
    }

}