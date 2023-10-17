package com.sheltersdog.user.dto.request

import jakarta.validation.constraints.NotBlank

data class PutUserProfileRequest(

    @field:NotBlank
    val nickname: String? = null,
    @field:NotBlank
    val profileImageUrl: String? = null,
)