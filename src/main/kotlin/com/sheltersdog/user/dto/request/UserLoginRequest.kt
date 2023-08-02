package com.sheltersdog.user.dto.request

import com.sheltersdog.core.model.SocialType

data class UserLoginRequest(
    val socialType: SocialType,
    val oauthId: String,
)
