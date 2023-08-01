package com.sheltersdog.user.dto.response

import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.user.entity.model.UserStatus

data class UserDto(
    val id: String,
    val name: String,
    val nickname: String,
    val profileImageUrl: String,

    val status: UserStatus = UserStatus.ACTIVE,

    val createdDate: String? = null,
    val modifyDate: String? = null,

    val shelters: List<ShelterDto>? = null,
)
