package com.sheltersdog.oauth

import com.sheltersdog.core.model.AUTHORIZATION
import com.sheltersdog.core.properties.KakaoProperties
import com.sheltersdog.oauth.dto.KakaoOauthTokenDto
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class OauthClient @Autowired constructor(
    val webClient: WebClient,
    val kakaoProperties: KakaoProperties,
) {
    val log = LoggerFactory.getLogger(this::class.java)

    fun getKakaoToken(code: String): Mono<KakaoOauthTokenDto> {
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
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    return@exchangeToMono response.bodyToMono(KakaoOauthTokenDto::class.java)
                }
                Mono.empty()
            }
    }

    fun getKakaoUserInfo(accessToken: String): Mono<KakaoUserInfoDto> {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("secure_resource", "true")

        return webClient.post()
            .uri(kakaoProperties.getUserInfoApi)
            .body(BodyInserters.fromFormData(body))
            .headers { headers ->
                headers[AUTHORIZATION] = "Bearer $accessToken"
            }.accept(MediaType.APPLICATION_JSON)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    return@exchangeToMono response.bodyToMono(KakaoUserInfoDto::class.java)
                }
                Mono.empty()
            }
    }
}