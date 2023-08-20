package com.sheltersdog.foreverdog.mapper

import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.mapper.toDto
import java.time.LocalDate

fun foreverdogToDto(
    entity: Foreverdog,
    isIncludeShelter: Boolean = false,
): ForeverdogDto {
    val shelter: ShelterDto? =
        if (isIncludeShelter && entity.shelter != null) entity.shelter.toDto()
        else null

    return ForeverdogDto(
        id = entity.id.toString(),
        status = entity.status,
        shelterId = entity.shelterId,
        shelter = shelter,
        announcementId = entity.announcementId,
        profileImageUrl = entity.profileImageUrl,
        gender = entity.gender,
        isNeutering = entity.isNeutering,
        neuteringDate = entity.neuteringDate?.let { localDateToKoreanFormat(it) },
        protectedStartDate = entity.protectedStartDate?.let { localDateToKoreanFormat(it) },
        name = entity.name,
        age = entity.birthYear?.let { LocalDate.now().year - it + 1 },
        breed = entity.breed,
        weight = entity.weight,
        socializationLevel = entity.socializationLevel,
        content = entity.content,
        histories = entity.histories,
    )
}
