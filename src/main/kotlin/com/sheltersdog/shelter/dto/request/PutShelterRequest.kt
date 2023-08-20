package com.sheltersdog.shelter.dto.request

import com.sheltersdog.shelter.entity.ShelterSns
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PutShelterRequest(
    val id: String,
    @field:NotBlank
    val name: String,
    val profileImageUrl: String,
    val contactNumber: String,

    @field:NotNull
    val regionCode: Long,
    @field:NotNull
    val detailAddress: String,
    val x: Double? = null,
    val y: Double? = null,

    val shelterSns: List<ShelterSns>,
    @field:NotNull
    val representativeSns: ShelterSns,

    val donationPath: String? = null,
    val donationUsageHistoryLink: String? = null,
    val isPrivateDetailAddress: Boolean = true,
)