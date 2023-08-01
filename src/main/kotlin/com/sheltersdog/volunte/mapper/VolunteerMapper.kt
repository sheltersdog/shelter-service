package com.sheltersdog.volunte.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.mapper.addressToDto
import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.core.util.localTimeKoreanFormat
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.mapper.shelterToDto
import com.sheltersdog.volunte.dto.response.VolunteerDto
import com.sheltersdog.volunte.entity.Volunteer

fun volunteerToDto(
    entity: Volunteer,
    isIncludeAddress: Boolean = false,
    isIncludeShelter: Boolean = false,
): VolunteerDto {
    val address: AddressDto? =
        if (isIncludeAddress && entity.address != null) addressToDto(entity.address)
        else null

    val shelter: ShelterDto? =
        if (isIncludeShelter && entity.shelter != null) shelterToDto(entity.shelter)
        else null

    return VolunteerDto(
        id = entity.id.toString(),
        shelterName = entity.shelterName,
        isShort = entity.isShort,
        categories = entity.categories,
        address = address,
        isAlwaysRecruiting = entity.isAlwaysRecruiting,
        startDate = entity.startDate?.let { localDateToKoreanFormat(it) },
        endDate = entity.endDate?.let { localDateToKoreanFormat(it) },
        startTime = entity.startTime?.let { localTimeKoreanFormat(it) },
        endTime = entity.endTime?.let { localTimeKoreanFormat(it) },
        content = entity.content,
        shelterId = entity.shelterId,
        shelter = shelter,
        days = entity.days,
        url = entity.url,
    )
}