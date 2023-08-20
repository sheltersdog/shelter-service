package com.sheltersdog.shelter.entity

import com.sheltersdog.address.entity.Address
import com.sheltersdog.shelter.entity.model.ShelterStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Shelter(
    @Id
    val id: ObjectId? = null,
    val name: String,
    val profileImageUrl: String,

    val contactNumber: String? = null,
    val isPrivateContact: Boolean = true,

    val regionCode: Long,
    val address: Address? = null,
    val detailAddress: String,
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
    val foreverdogCount: Int = 0,
    val sheltersAdminCount: Int = 1,

    val isVolunteerRecruiting: Boolean = false,
    val isDonationPossible: Boolean = false,

    val shelterAdminInvites: List<ShelterAdminInvite> = listOf(),
    val sheltersAdmins: List<ShelterJoinUser> = listOf(),

    val createdDate: LocalDate? = null,
    val modifyDate: LocalDate? = null,

    val searchKeyword: String,
)