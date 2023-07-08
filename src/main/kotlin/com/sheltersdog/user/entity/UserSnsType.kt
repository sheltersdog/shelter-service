package com.sheltersdog.user.entity

import com.sheltersdog.core.model.SocialType

data class UserSnsType(
    val type: SocialType,
    val oauthId: String,
    val loginId: String,
    val token: String,
)
