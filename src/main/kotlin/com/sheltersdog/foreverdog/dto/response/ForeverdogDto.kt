package com.sheltersdog.foreverdog.dto.response

import com.sheltersdog.core.model.Gender
import com.sheltersdog.foreverdog.entity.ForeverdogHistory
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.entity.model.SocializationLevel
import com.sheltersdog.shelter.dto.response.ShelterDto

data class ForeverdogDto(
    val id: String,
    val status: ForeverdogStatus = ForeverdogStatus.SHELTER_PROTECTION,

    val shelterId: String,
    val shelter: ShelterDto? = null,

    // 공고번호
    val announcementId: String,
    val profileImageUrl: String,
    val gender: Gender,
    // 중성화 여부
    val isNeutering: Boolean = false,
    // 중성화 일자
    val neuteringDate: String? = null,
    // 쉼터 입소일
    val protectedStartDate: String? = null,

    val name: String? = null,
    val age: Int? = null,

    // 견종
    val breed: String? = null,
    val weight: Double? = null,

    // 사회화 정도
    val socializationLevel: SocializationLevel = SocializationLevel.NOT_CHECKED,
    val content: String? = null,

    val histories: List<ForeverdogHistory> = listOf(),
)
