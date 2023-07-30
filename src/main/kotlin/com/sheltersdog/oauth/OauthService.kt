package com.sheltersdog.oauth

import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class OauthService @Autowired constructor(
    val oauthClient: OauthClient,
    val userRepository: UserRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun redirectKakao(code: String): Mono<KakaoUserInfoDto?> {
        var kakaoUserInfoDto: KakaoUserInfoDto? = null

        return oauthClient.getKakaoToken(code)
            .flatMap { body ->
                log.debug("body: $body")
                oauthClient.getKakaoUserInfo(body.accessToken)
            }.flatMap { body ->
                log.debug("body: $body")
                kakaoUserInfoDto = body
                userRepository.isExistUser(body.id, body.kakaoAccount.email, UserStatus.ACTIVE)
            }.mapNotNull { isExist ->
                kakaoUserInfoDto?.copy(isShelterUser = isExist)
            }.switchIfEmpty {
                Mono.error { throw SheltersdogException("Login Error...!!") }
            }.doOnError { error ->
                log.error("redirect kakao error, code: $code", error)
            }
    }

}