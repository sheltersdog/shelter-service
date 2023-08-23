package com.sheltersdog.image.repository

import com.sheltersdog.image.entity.Image
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

}