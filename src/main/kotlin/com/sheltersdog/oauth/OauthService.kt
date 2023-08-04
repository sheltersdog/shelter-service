package com.sheltersdog.oauth

import com.sheltersdog.core.model.SocialType
import com.sheltersdog.oauth.dto.KakaoUserInfoDto
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OauthService @Autowired constructor(
    val oauthClient: OauthClient,
    val userRepository: UserRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun redirectKakao(code: String): KakaoUserInfoDto {
        val kakaoToken = oauthClient.getKakaoToken(code)

        log.debug("kakaoToken: $kakaoToken")
        val kakaoUserInfo = oauthClient.getKakaoUserInfo(kakaoToken.accessToken)

        log.debug("kakaoUserInfo: $kakaoUserInfo")
        val isExist = userRepository.isExistUser(kakaoUserInfo.id.toString(), kakaoUserInfo.kakaoAccount.email, UserStatus.ACTIVE)
        return kakaoUserInfo.copy(isShelterUser = isExist)
    }

    suspend fun leaveKakao(id: Long) {
        userRepository.changeAllUserStatusByOauthIdAndSocialType(
            kakaoOauthId = id.toString(),
            socialType = SocialType.KAKAO
        )
    }

}