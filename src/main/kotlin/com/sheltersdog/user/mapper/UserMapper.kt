package com.sheltersdog.user.mapper

import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.mapper.toDto
import com.sheltersdog.user.dto.response.UserDto
import com.sheltersdog.user.entity.User

fun userToDto(
    entity: User,
    isIncludeShelter: Boolean = false,
) {
    val shelters: List<ShelterDto>? = if (isIncludeShelter) {
        entity.userJoinShelters.filter { userJoinShelter ->
            userJoinShelter.shelter != null
        }.map { userJoinShelter ->
            userJoinShelter.shelter!!.toDto()
        }.toList()
    } else null

    UserDto(
        id = entity.id.toString(),
        name = entity.name,
        nickname = entity.nickname,
        profileImageUrl = entity.profileImageUrl,
        status = entity.status,
        shelters = shelters,
        createdDate = entity.createdDate?.let { localDateToKoreanFormat(it) },
        modifyDate = entity.modifyDate?.let { localDateToKoreanFormat(it) },
    )
}