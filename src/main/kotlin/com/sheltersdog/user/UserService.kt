package com.sheltersdog.user

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.security.jwt.JwtProvider
import com.sheltersdog.user.dto.request.UserJoinRequest
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDate

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository,
    val jwtProvider: JwtProvider,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun postUser(requestBody: UserJoinRequest): Mono<JwtDto> {
        val nickname = requestBody.nickname.ifBlank { "서비스 유저" }
        val profileImageUrl = requestBody.profileImageUrl.ifBlank { "https://www.figma.com/file/CB5J2Dd0r2H3DkQB10L3Up/%EC%9C%A0%EB%B0%98-Copy?type=design&node-id=253-6834&mode=design&t=u9UAAyYULdZwPhRU-4" }

        return userRepository.save(
            User(
                oauthId = requestBody.oauthId,
                name = "",
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                status = UserStatus.ACTIVE,
                createdDate = LocalDate.now(),
                modifyDate = LocalDate.now(),
                isAgreeServiceTerm = true,
                serviceTermAgreeDate = LocalDate.now(),
            )
        ).map { user -> jwtProvider.generateToken(id = user.id.toString())
        }.switchIfEmpty {
            Mono.error { throw SheltersdogException("Join Error...!!") }
        }.doOnError { error ->
            log.error("login error, requestBody: $requestBody", error)
        }
    }
}