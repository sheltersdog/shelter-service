package com.sheltersdog.oauth

import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.properties.KakaoProperties
import com.sheltersdog.oauth.dto.KakaoOauthTokenDto
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("/oauth")
class OauthController @Autowired constructor(
    val oauthService: OauthService,
    val oauthClient: OauthClient,
    val kakaoProperties: KakaoProperties,
){
    val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/kakao")
    fun redirectKakao(@RequestParam("code") code: String): Mono<KakaoUserInfoDto> {
        log.info("/oauth/kakao")
        return oauthClient.getKakaoToken(code)
            .flatMap { body ->
                log.info(body.toString())
                oauthClient.getKakaoUserInfo(body.accessToken)
            }.flatMap { body ->
                log.info(body.toString())
                Mono.just(body)
            }.switchIfEmpty {
                log.error("error!!")
                Mono.empty()
            }
    }

    @GetMapping("/kakao/leave")
    fun leaveKakao(
        @RequestParam("user_id") id: Long,
        @RequestParam("referrer_type") referrerType: String,
        @RequestHeader("Authorization") authorization: String,) {
        if (authorization.isBlank() || authorization != "KakaoAK ${kakaoProperties.adminKey}") {
            throw SheltersdogException(
                message = "Authorization is wrong",
                httpStatus = HttpStatus.BAD_REQUEST,
            )
        }
    }

}