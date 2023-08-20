package com.sheltersdog.volunteer.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.mapper.addressToDto
import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.mapper.toDto
import com.sheltersdog.volunteer.dto.response.VolunteerDto
import com.sheltersdog.volunteer.entity.Volunteer

fun Volunteer.toDto(
    isIncludeAddress: Boolean = false,
    isIncludeShelter: Boolean = false,
): VolunteerDto {
    val address: AddressDto? =
        if (isIncludeAddress && this.address != null) addressToDto(this.address)
        else null

    val shelter: ShelterDto? =
        if (isIncludeShelter && this.shelter != null) this.shelter.toDto()
        else null

    return VolunteerDto(
        id = this.id.toString(),
        sourceType = this.sourceType,
        shelterName = this.shelterName,
        isShort = this.isShort,
        categories = this.categories,
        address = address,
        isAlwaysRecruiting = this.isAlwaysRecruiting,
        startDate = this.startDate?.let { localDateToKoreanFormat(it) },
        endDate = this.endDate?.let { localDateToKoreanFormat(it) },
        exposeStartDate = this.exposeStartDate?.let { localDateToKoreanFormat(it) },
        exposeEndDate = this.exposeEndDate?.let { localDateToKoreanFormat(it) },
        startTime = this.startTime,
        endTime = this.endTime,
        content = this.content,
        shelterId = this.shelterId,
        shelter = shelter,
        days = this.days,
        url = this.url,
    )
}