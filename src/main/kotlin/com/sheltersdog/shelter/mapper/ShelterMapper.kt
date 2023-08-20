package com.sheltersdog.shelter.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.mapper.addressToDto
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.Shelter

fun Shelter.toDto(
    isIncludeAddress: Boolean = false,
    isIncludeSheltersAdmin: Boolean = false,
): ShelterDto {
    val address: AddressDto? =
        if (isIncludeAddress && this.address != null) addressToDto(this.address)
        else null

    val detailAddress =
        if (this.isPrivateDetailAddress) null
        else this.detailAddress

    val sheltersAdmins =
        if (isIncludeSheltersAdmin && this.sheltersAdmins.isNotEmpty()) {
            this.sheltersAdmins.map { shelterJoinUser ->
                shelterJoinUserToDto(shelterJoinUser)
            }
        } else listOf()

    return ShelterDto(
        id = this.id.toString(),
        name = this.name,
        profileImageUrl = this.profileImageUrl,
        contactNumber = this.contactNumber,
        isPrivateContact = this.isPrivateContact,
        address = address,
        detailAddress = detailAddress,
        x = this.x,
        y = this.y,
        isPrivateDetailAddress = this.isPrivateDetailAddress,

        shelterSns = this.shelterSns,
        representativeSns = this.representativeSns,

        donationPath = this.donationPath,
        donationUsageHistoryLink = this.donationUsageHistoryLink,

        status = this.status,

        volunteerTotalCount = this.volunteerTotalCount,
        volunteerActiveCount = this.volunteerActiveCount,
        volunteerInactiveCount = this.volunteerInactiveCount,
        foreverdogCount = this.foreverdogCount,
        sheltersAdminCount = this.sheltersAdminCount,

        isVolunteerRecruiting = this.isVolunteerRecruiting,
        isDonationPossible = this.isDonationPossible,

        sheltersAdmins = sheltersAdmins,

        createdDate = this.createdDate,
        modifyDate = this.modifyDate,
    )
}