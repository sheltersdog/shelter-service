package com.sheltersdog.oauth

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.AUTHORIZATION
import com.sheltersdog.core.properties.KakaoProperties
import com.sheltersdog.oauth.dto.KakaoOauthTokenDto
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@Component
class OauthClient @Autowired constructor(
    val webClient: WebClient,
    val kakaoProperties: KakaoProperties,
) {
    val log = LoggerFactory.getLogger(this::class.java)

    suspend fun getKakaoToken(code: String): KakaoOauthTokenDto {
        val grantType = "authorization_code"
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", grantType)
        body.add("client_id", kakaoProperties.apiKey)
        body.add("redirect_uri", kakaoProperties.redirectUri)
        body.add("code", code)

        return webClient.post()
            .uri(kakaoProperties.getTokenApi)
            .body(BodyInserters.fromFormData(body))
            .headers { headers ->
                headers[AUTHORIZATION] = "KakaoAK ${kakaoProperties.adminKey}"
            }.accept(MediaType.APPLICATION_JSON)
            .awaitExchange { response ->
                // body를 nullable로 설정할지 고민 필요
                if (response.statusCode() == HttpStatus.OK) {
                    return@awaitExchange response.toEntity(KakaoOauthTokenDto::class.java)
                        .awaitSingle()
                        .body!!
                }

                throw SheltersdogException(
                    type = ExceptionType.NOT_FOUND_KAKAO_TOKEN,
                    variables = mapOf(
                        "statusCode" to response.statusCode(),
                        "body" to response.awaitBody(),
                    ),
                )
            }
    }

    suspend fun getKakaoUserInfo(accessToken: String): KakaoUserInfoDto {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("secure_resource", "true")

        return webClient.post()
            .uri(kakaoProperties.getUserInfoApi)
            .body(BodyInserters.fromFormData(body))
            .headers { headers ->
                headers[AUTHORIZATION] = "Bearer $accessToken"
            }.accept(MediaType.APPLICATION_JSON)
            .awaitExchange { response ->
                // TODO body를 nullable로 설정할지 고민 필요
                if (response.statusCode() == HttpStatus.OK) {
                    return@awaitExchange response.toEntity(KakaoUserInfoDto::class.java)
                        .awaitSingle()
                        .body!!
                }

                throw SheltersdogException(
                    type = ExceptionType.NOT_FOUND_KAKAO_USER_INFO,
                    variables = mapOf(
                        "accessToken" to accessToken,
                        "statusCode" to response.statusCode(),
                        "body" to response.awaitBody(),
                    )
                )
            }
    }
}