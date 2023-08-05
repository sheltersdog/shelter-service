package com.sheltersdog.shelter.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.mapper.addressToDto
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.Shelter

fun shelterToDto(
    entity: Shelter,
    isIncludeAddress: Boolean = false,
    isIncludeSheltersAdmin: Boolean = false,
): ShelterDto {
    val address: AddressDto? =
        if (isIncludeAddress && entity.address != null) addressToDto(entity.address)
        else null

    val detailAddress =
        if (entity.isPrivateDetailAddress) null
        else entity.detailAddress

    val sheltersAdmins =
        if (isIncludeSheltersAdmin && entity.sheltersAdmins.isNotEmpty()) {
            entity.sheltersAdmins.map { shelterJoinUser ->
                shelterJoinUserToDto(shelterJoinUser)
            }
        } else listOf()

    return ShelterDto(
        id = entity.id.toString(),
        name = entity.name,
        profileImageUrl = entity.profileImageUrl,
        contactNumber = entity.contactNumber,
        isPrivateContact = entity.isPrivateContact,
        address = address,
        detailAddress = detailAddress,
        x = entity.x,
        y = entity.y,
        isPrivateDetailAddress = entity.isPrivateDetailAddress,

        shelterSns = entity.shelterSns,
        representativeSns = entity.representativeSns,

        donationPath = entity.donationPath,
        donationUsageHistoryLink = entity.donationUsageHistoryLink,

        status = entity.status,

        volunteerTotalCount = entity.volunteerTotalCount,
        volunteerActiveCount = entity.volunteerActiveCount,
        volunteerInactiveCount = entity.volunteerInactiveCount,
        foreverdogCount = entity.foreverdogCount,
        sheltersAdminCount = entity.sheltersAdminCount,

        isVolunteerRecruiting = entity.isVolunteerRecruiting,
        isDonationPossible = entity.isDonationPossible,

        sheltersAdmins = sheltersAdmins,

        createdDate = entity.createdDate,
        modifyDate = entity.modifyDate,
    )
}