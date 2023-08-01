package com.sheltersdog.shelter.mapper

import com.sheltersdog.shelter.dto.response.ShelterJoinUserDto
import com.sheltersdog.shelter.entity.ShelterJoinUser

fun shelterJoinUserToDto(entity: ShelterJoinUser): ShelterJoinUserDto {
    return ShelterJoinUserDto(
        name = entity.name,
        nickname = entity.nickname,
        authorities = entity.authorities,
        userId = entity.userId,
        email = entity.email,
        status = entity.status,
    )
}