package com.sheltersdog.foreverdog.entity

import com.sheltersdog.core.model.Gender
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.entity.model.SocializationLevel
import com.sheltersdog.shelter.entity.Shelter
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Foreverdog(
    @Id
    val id: ObjectId? = null,
    val status: ForeverdogStatus = ForeverdogStatus.SHELTER_PROTECTION,

    val shelterId: String,
    val shelter: Shelter? = null,

    // 공고번호
    val announcementId: String,
    val profileImageUrl: String,
    val gender: Gender,
    // 중성화 여부
    val isNeutering: Boolean = false,
    // 중성화 일자
    val neuteringDate: LocalDate? = null,
    val protectedStartDate: LocalDate? = null,

    val name: String? = null,
    val birthYear: Int? = null,

    // 견종
    val breed: String? = null,
    val weight: Double? = null,

    // 사회화 정도
    val socializationLevel: SocializationLevel = SocializationLevel.NOT_CHECKED,
    val content: String? = null,

    val histories: List<ForeverdogHistory> = listOf(),

    val searchKeyword: String,
)
