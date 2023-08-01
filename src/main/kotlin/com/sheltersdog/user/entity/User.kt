package com.sheltersdog.user.entity

import com.sheltersdog.user.entity.model.UserStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class User(
    @Id
    val id: ObjectId? = null,
    val oauthId: String,
    val name: String,
    val nickname: String,
    val profileImageUrl: String,

    val status: UserStatus = UserStatus.ACTIVE,
    val userJoinShelters: List<UserJoinShelter> = listOf(),

    val isAgreeServiceTerm: Boolean = true,
    val serviceTermAgreeDate: LocalDate? = null,

    val createdDate: LocalDate? = null,
    val modifyDate: LocalDate? = null,
)