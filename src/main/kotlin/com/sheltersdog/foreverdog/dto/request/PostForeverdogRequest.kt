package com.sheltersdog.foreverdog.dto.request

import com.sheltersdog.core.model.Gender
import com.sheltersdog.foreverdog.entity.ForeverdogHistory
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.entity.model.SocializationLevel

data class PostForeverdogRequest(
    val shelterId: String,
    val status: ForeverdogStatus = ForeverdogStatus.SHELTER_PROTECTION,
    val announcementId: String,
    val profileImageUrl: String,
    val gender: Gender,
    val isNeutering: Boolean = false,
    val neuteringDate: String? = null,
    val protectedStartDate: String? = null,
    val name: String? = null,
    val birthYear: Int? = null,
    val bread: String? = null,
    val weight: Double? = null,

    val socializationLevel: SocializationLevel = SocializationLevel.NOT_CHECKED,
    val content: String? = null,

    val histories: List<ForeverdogHistory> = listOf(),
)
