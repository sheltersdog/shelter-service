package com.sheltersdog.foreverdog.mapper

import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.mapper.toDto
import java.time.LocalDate

fun Foreverdog.toDto(
    isIncludeShelter: Boolean = false,
): ForeverdogDto {
    val shelter: ShelterDto? =
        if (isIncludeShelter && this.shelter != null) this.shelter.toDto()
        else null

    return ForeverdogDto(
        id = this.id.toString(),
        status = this.status,
        shelterId = this.shelterId,
        shelter = shelter,
        announcementId = this.announcementId,
        profileImageUrl = this.profileImageUrl,
        gender = this.gender,
        isNeutering = this.isNeutering,
        neuteringDate = this.neuteringDate?.let { localDateToKoreanFormat(it) },
        protectedStartDate = this.protectedStartDate?.let { localDateToKoreanFormat(it) },
        name = this.name,
        age = this.birthYear?.let { LocalDate.now().year - it + 1 },
        breed = this.breed,
        weight = this.weight,
        socializationLevel = this.socializationLevel,
        content = this.content,
        histories = this.histories,
    )
}
