package com.sheltersdog.user.repository

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.model.SocialType
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun isExistUser(oauthId: String, email: String, status: UserStatus): Mono<Boolean> {
        val query = Query.query(
            where(User::kakaoOauthId).`is`(oauthId).and(User::status).`is`(status)
        )

        return reactiveMongoTemplate.exists(query, User::class.java)
    }

    fun findById(userId: String): Mono<User> {
        return reactiveMongoTemplate.findById(userId, User::class.java)
    }

    fun save(user: User): Mono<User> {
        return reactiveMongoTemplate.save(user)
    }

    fun findByOauthIdAndSocialType(kakaoOauthId: String, socialType: SocialType): Mono<User> {
        return reactiveMongoTemplate.findOne(
            Query.query(
                where(User::kakaoOauthId).`is`(kakaoOauthId)
                    .and(User::socialType).`is`(socialType)
                    .and(User::status).`is`(UserStatus.ACTIVE)
            ), User::class.java
        )
    }
}