package com.sheltersdog.user

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.SocialType
import com.sheltersdog.core.security.jwt.JwtProvider
import com.sheltersdog.user.dto.request.UserJoinRequest
import com.sheltersdog.user.dto.request.UserLoginRequest
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository,
    val jwtProvider: JwtProvider,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun postUser(requestBody: UserJoinRequest): JwtDto {
        val nickname = requestBody.nickname.ifBlank { "서비스 유저" }
        val profileImageUrl =
            requestBody.profileImageUrl.ifBlank { "https://www.figma.com/file/CB5J2Dd0r2H3DkQB10L3Up/%EC%9C%A0%EB%B0%98-Copy?type=design&node-id=253-6834&mode=design&t=u9UAAyYULdZwPhRU-4" }

        val isExist = userRepository.isExistUser(requestBody.oauthId, requestBody.email, UserStatus.ACTIVE)
        if (isExist) {
            log.debug("fun postUser -> Already exist user, requestBody: $requestBody")
            throw SheltersdogException("이미 가입된 유저입니다.")
        }

        val kakaoOauthId = if (requestBody.socialType == SocialType.KAKAO) {
            requestBody.oauthId
        } else null

        val user = userRepository.save(
            User(
                socialType = requestBody.socialType,
                kakaoOauthId = kakaoOauthId,
                name = requestBody.name,
                email = requestBody.email,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                status = UserStatus.ACTIVE,
                createdDate = LocalDate.now(),
                modifyDate = LocalDate.now(),
                isAgreeServiceTerm = true,
                serviceTermAgreeDate = LocalDate.now(),
            )
        )
        return jwtProvider.generateToken(id = user.id.toString())
    }

    suspend fun login(requestBody: UserLoginRequest): JwtDto {
        val user = userRepository.findByOauthIdAndSocialType(
            kakaoOauthId = requestBody.oauthId,
            socialType = requestBody.socialType,
        )

        if (user == null) {
            log.debug("login info is wrong. requestBody: $requestBody")
            throw SheltersdogException("존재하지 않는 유저입니다.")
        }

        return jwtProvider.generateToken(id = user.id.toString())
    }
}