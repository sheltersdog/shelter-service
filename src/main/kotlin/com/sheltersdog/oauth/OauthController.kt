package com.sheltersdog.oauth

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.properties.KakaoProperties
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth")
class OauthController @Autowired constructor(
    val oauthService: OauthService,
    val kakaoProperties: KakaoProperties,
) {

    @GetMapping("/kakao")
    suspend fun redirectKakao(@RequestParam("code") code: String): KakaoUserInfoDto {
        return oauthService.redirectKakao(code)
    }

    @GetMapping("/kakao/leave")
    suspend fun leaveKakao(
        @RequestParam("user_id") id: Long,
        @RequestParam("referrer_type") referrerType: String,
        @RequestHeader("Authorization") authorization: String,
    ) {
        if (authorization.isBlank() || authorization != "KakaoAK ${kakaoProperties.adminKey}") {
            throw SheltersdogException(
                type = ExceptionType.KAKAO_LEAVE,
                variables = mapOf(
                    "authorization" to authorization,
                    "user_id" to id,
                    "referrer_type" to referrerType
                ),
            )
        }
        oauthService.leaveKakao(id)
    }

}