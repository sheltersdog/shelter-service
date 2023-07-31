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
    val profileImageUrl: String = "",

    val contactNumber: String = "",
    val isPrivateContact: Boolean = true,

    val address: Address?,
    val detailAddress: String,
    val x: Double? = null,
    val y: Double? = null,
    val isPrivateDetailAddress: Boolean = true,

    val shelterSns: List<ShelterSns>,
    val representativeSns: ShelterSns,

    val donationPath: String = "",
    val donationUsageHistoryLink: String = "",

    val status: ShelterStatus = ShelterStatus.ACTIVE,

    val volunteerTotalCount: Int = 0,
    val volunteerActiveCount: Int = 0,
    val volunteerInactiveCount: Int = 0,
    val sheltersdogCount: Int = 0,
    val sheltersAdminCount: Int = 1,

    val isVolunteerRecruiting: Boolean = false,
    val isDonationPossible: Boolean = false,

    val sheltersAdmins: List<ShelterJoinUser> = listOf(),

    val createdDate: LocalDate?,
    val modifyDate: LocalDate?,
)