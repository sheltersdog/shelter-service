package com.sheltersdog.user.repository

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.model.SocialType
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.*
import org.springframework.data.mongodb.core.updateMulti
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserRepository @Autowired constructor(
    val reactiveMongoTemplate: ReactiveMongoTemplate) {

    suspend fun isExistUser(oauthId: String, email: String, status: UserStatus): Boolean {
        val query = Query.query(
            where(User::kakaoOauthId).`is`(oauthId).and(User::status).`is`(status)
        )

        return reactiveMongoTemplate.exists(query, User::class.java).awaitSingle()
    }

    suspend fun findById(userId: String): User {
        return reactiveMongoTemplate.findById(userId, User::class.java).awaitSingle()
    }

    suspend fun save(user: User): User {
        return reactiveMongoTemplate.save(user).awaitSingle()
    }

    suspend fun findByOauthIdAndSocialType(kakaoOauthId: String, socialType: SocialType): User {
        return reactiveMongoTemplate.findOne(
            Query.query(
                where(User::kakaoOauthId).`is`(kakaoOauthId)
                    .and(User::socialType).`is`(socialType)
                    .and(User::status).`is`(UserStatus.ACTIVE)
            ), User::class.java
        ).awaitSingle()
    }

    suspend fun changeAllUserStatusByOauthIdAndSocialType(kakaoOauthId: String, socialType: SocialType): UpdateResult? {
        return reactiveMongoTemplate.updateMulti(
            Query.query(
                where(User::kakaoOauthId).`is`(kakaoOauthId)
                    .and(User::socialType).`is`(socialType)
                    .and(User::status).`is`(UserStatus.ACTIVE)
            ), Update().set(User::status.toString(), UserStatus.INACTIVE),
            User::class.java
        ).awaitSingle()
    }
}