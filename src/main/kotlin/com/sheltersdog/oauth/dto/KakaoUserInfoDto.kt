package com.sheltersdog.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoDto(
    val isShelterUser: Boolean = false,
    @field:JsonProperty(value = "id")
    val id: Long = 0,
    @field:JsonProperty(value = "connected_at")
    val connectedAt: String = "",
    @field:JsonProperty(value = "properties")
    val properties: Properties = Properties(),
    @field:JsonProperty(value = "kakao_account")
    val kakaoAccount: KakaoAccount = KakaoAccount(),
)

data class Properties(
    val nickname: String = "",
    @field:JsonProperty(value = "profile_image")
    val profileImage: String = "",
    @field:JsonProperty(value = "thumbnail_image")
    val thumbnailImage: String = "",
)

data class KakaoAccount(
    val email: String = "",
    val profile: Profile = Profile(),
)

data class Profile(
    val nickname: String = "",
    @field:JsonProperty(value = "thumbnail_image_url")
    val thumbnailImageUrl: String = "",
    @field:JsonProperty(value = "profile_image_url")
    val profileImageUrl: String = "",
    @field:JsonProperty(value = "is_default_image")
    val isDefaultImage: Boolean = false,
)
