package com.sheltersdog.shelter.dto.response

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.shelter.entity.ShelterSns
import com.sheltersdog.shelter.entity.model.ShelterStatus
import java.time.LocalDate

data class ShelterDto(
    val id: String,
    val name: String,
    val profileImageUrl: String,

    val contactNumber: String? = null,
    val isPrivateContact: Boolean = true,

    val address: AddressDto? = null,
    val detailAddress: String? = null,
    val x: Double? = null,
    val y: Double? = null,
    val isPrivateDetailAddress: Boolean = true,

    val shelterSns: List<ShelterSns>,
    val representativeSns: ShelterSns,

    val donationPath: String? = null,
    val donationUsageHistoryLink: String? = null,

    val status: ShelterStatus = ShelterStatus.ACTIVE,

    val volunteerTotalCount: Int = 0,
    val volunteerActiveCount: Int = 0,
    val volunteerInactiveCount: Int = 0,
    val sheltersdogCount: Int = 0,
    val sheltersAdminCount: Int = 1,

    val isVolunteerRecruiting: Boolean = false,
    val isDonationPossible: Boolean = false,

    val sheltersAdmins: List<ShelterJoinUserDto>? = null,

    val createdDate: LocalDate? = null,
    val modifyDate: LocalDate? = null,
)
