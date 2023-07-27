package com.sheltersdog.image.repository

import com.mongodb.client.result.DeleteResult
import com.sheltersdog.image.entity.Image
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class ImageRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun save(image: Image): Mono<Image> {
        return reactiveMongoTemplate.save(image)
    }

    fun deleteById(id: ObjectId): Mono<DeleteResult> {
        return reactiveMongoTemplate.findById(id, Image::class.java).flatMap {
            reactiveMongoTemplate.remove(it)
        }
    }

}