package com.sheltersdog.user.entity

import com.sheltersdog.core.model.Gender
import com.sheltersdog.image.entity.Image
import com.sheltersdog.user.entity.model.UserStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class User(
    @Id
    val id: ObjectId? = null,
    val name: String = "",
    val nickname: String = "",
    val introduce: String = "",
    val gender: Gender = Gender.PRIVATE,
    val state: UserStatus = UserStatus.ACTIVE,
    val phoneNumber: String = "",
    val fcmToken: String = "",

    val profileImageUrl: String = "",

    val userSns: List<UserSnsType> = listOf(),
    val shelters: List<UserJoinShelter> = listOf(),

    @DBRef(lazy = true)
    val images: List<Image> = listOf(),
    @DBRef(lazy = true)
    val userHistory: List<UserHistory> = listOf(),

    val lastLoginTime: LocalDateTime = LocalDateTime.now(),
    val joinDate: LocalDate = LocalDate.now(),

    @Version
    val version: Long,
)