package com.sheltersdog.image.repository

import com.sheltersdog.image.entity.Image
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ImageRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    suspend fun save(image: Image): Image {
        return reactiveMongoTemplate.save(image).awaitSingle()
    }

}