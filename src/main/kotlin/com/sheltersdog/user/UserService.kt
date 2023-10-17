package com.sheltersdog.user

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.SocialType
import com.sheltersdog.core.security.jwt.JwtProvider
import com.sheltersdog.core.util.ifUpdateFailThrow
import com.sheltersdog.user.dto.request.PutUserProfileRequest
import com.sheltersdog.user.dto.request.UserJoinRequest
import com.sheltersdog.user.dto.request.UserLoginRequest
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.reflect.KProperty

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
            throw SheltersdogException(
                type = ExceptionType.ALREADY_JOIN_USER,
                variables = mapOf("requestBody" to requestBody)
            )
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
        ) ?: throw SheltersdogException(
            type = ExceptionType.NOT_FOUND_USER,
            variables = mapOf("requestBody" to requestBody)
        )

        return jwtProvider.generateToken(id = user.id.toString())
    }

    suspend fun putProfile(requestBody: PutUserProfileRequest) {
        val userId =
            (ReactiveSecurityContextHolder.getContext()
                .awaitSingle().authentication.principal as org.springframework.security.core.userdetails.User).username

        val updateFields = mutableMapOf<KProperty<*>, Any?>()

        if (requestBody.nickname !== null) {
            updateFields[User::nickname] = requestBody.nickname
        }

        if (requestBody.profileImageUrl !== null) {
            updateFields[User::profileImageUrl] = requestBody.profileImageUrl
        }

        if (updateFields.isEmpty()) return

        userRepository.updateById(
            id = userId,
            updateFields = updateFields,
        ).ifUpdateFailThrow(
            tableName = User::class.java.name,
            variables = mapOf("requestBody" to requestBody),
        )
    }
}