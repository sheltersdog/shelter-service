package com.sheltersdog.user.dto.request

import com.sheltersdog.core.model.SocialType
import jakarta.validation.constraints.NotBlank

data class UserJoinRequest(
    val name: String? = null,
    @field:NotBlank
    val oauthId: String,
    @field:NotBlank
    val socialType: SocialType,
    @field:NotBlank
    val email: String,
    val nickname: String = "",
    val profileImageUrl: String = "",
)
