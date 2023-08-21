package com.sheltersdog.user.entity

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.SocialType
import com.sheltersdog.user.entity.model.UserStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class User(
    @Id
    val id: ObjectId? = null,
    val socialType: SocialType,
    val kakaoOauthId: String? = null,
    val email: String,
    val name: String? = null,
    val nickname: String,
    val profileImageUrl: String,

    val status: UserStatus = UserStatus.ACTIVE,
    val userJoinShelters: List<UserJoinShelter> = listOf(),

    val isAgreeServiceTerm: Boolean = true,
    val serviceTermAgreeDate: LocalDate? = null,

    val createdDate: LocalDate? = null,
    val modifyDate: LocalDate? = null,
)

fun User?.ifNullOrNotActiveThrow(variables: Map<String, Any?>): User {
    if (this != null && this.status == UserStatus.ACTIVE) return this
    throw SheltersdogException(
        type = ExceptionType.NOT_FOUND_USER,
        variables = variables,
    )
}