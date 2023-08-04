package com.sheltersdog.image.repository

import com.mongodb.client.result.DeleteResult
import com.sheltersdog.image.entity.Image
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class ImageRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    suspend fun save(image: Image): Image {
        return reactiveMongoTemplate.save(image).awaitSingle()
    }

    suspend fun deleteById(id: ObjectId): DeleteResult {
        val image = reactiveMongoTemplate.findById(id, Image::class.java).awaitSingle()
        return reactiveMongoTemplate.remove(image).awaitSingle()
    }

}