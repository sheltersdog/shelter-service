package com.sheltersdog.shelter.dto.request

import com.sheltersdog.shelter.entity.model.ShelterAuthority
import jakarta.validation.constraints.NotNull

data class PostShelterAdminInviteRequest(
    @field:NotNull
    val shelterId: String,
    @field:NotNull
    val email: String,
    @field:NotNull
    val authorities: List<ShelterAuthority>,
)
