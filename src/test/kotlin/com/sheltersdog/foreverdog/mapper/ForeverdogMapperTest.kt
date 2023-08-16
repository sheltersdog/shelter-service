package com.sheltersdog.foreverdog.mapper

import com.sheltersdog.core.model.Gender
import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.foreverdog.entity.ForeverdogHistory
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.entity.model.SocializationLevel
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.*

class ForeverdogMapperTest {

    @MethodSource
    @ParameterizedTest(name = "entity: {0}")
    fun foreverdogToDtoTest(entity: Foreverdog) {
        val dto = foreverdogToDto(entity)
        assertEquals(entity.id.toString(), dto.id)
        assertEquals(entity.status, dto.status)
        assertEquals(entity.shelterId, dto.shelterId)
        assertEquals(entity.announcementId, dto.announcementId)
        assertEquals(entity.profileImageUrl, dto.profileImageUrl)
        assertEquals(entity.gender, dto.gender)
        assertEquals(entity.isNeutering, dto.isNeutering)
        assertEquals(entity.neuteringDate?.let { localDateToKoreanFormat(it) }, dto.neuteringDate)
        assertEquals(entity.protectedStartDate?.let { localDateToKoreanFormat(it) }, dto.protectedStartDate)
        assertEquals(entity.name, dto.name)
        assertEquals(entity.birthYear?.let { LocalDate.now().year - it + 1 }, dto.age)
        assertEquals(entity.breed, dto.breed)
        assertEquals(entity.weight, dto.weight)
        assertEquals(entity.socializationLevel, dto.socializationLevel)
        assertEquals(entity.content, dto.content)

        if (dto.histories.isNotEmpty()) {
            assertEquals(entity.histories.size, dto.histories.size)
            assertEquals(entity.histories, dto.histories)
        }
    }

    companion object {
        @JvmStatic
        fun foreverdogToDtoTest() = listOf(
            Arguments.of(
                Foreverdog(
                    id = ObjectId.get(),
                    status = ForeverdogStatus.ADOPT,
                    shelterId = ObjectId.get().toString(),
                    announcementId = UUID.randomUUID().toString(),
                    profileImageUrl = "https://profileimage.com",
                    gender = Gender.MAN,
                    isNeutering = false,
                    neuteringDate = null,
                    protectedStartDate = LocalDate.of(2023, 6, 23),
                    name = "벤",
                    birthYear = 2022,
                    breed = "도베르만",
                    weight = 18.0,
                    socializationLevel = SocializationLevel.GOOD,
                    content = "도베르만이에요. 뛸때 말처럼 뛰어서 귀여워요.",
                    histories = listOf(
                        ForeverdogHistory("보호소 입소: 2023년 6월 경", LocalDate.of(2023, 6, 23)),
                        ForeverdogHistory("입양: 2023년 7월 경", LocalDate.of(2023, 7, 7)),
                    ),
                    searchKeyword = ""
                )
            ),
            Arguments.of(
                Foreverdog(
                    id = ObjectId.get(),
                    status = ForeverdogStatus.ADOPT,
                    shelterId = ObjectId.get().toString(),
                    announcementId = UUID.randomUUID().toString(),
                    profileImageUrl = "https://profileimage.com",
                    gender = Gender.MAN,
                    isNeutering = false,
                    neuteringDate = null,
                    protectedStartDate = LocalDate.now().minusMonths(1),
                    name = "도담",
                    birthYear = 2023,
                    breed = null,
                    weight = null,
                    socializationLevel = SocializationLevel.ATTENTION,
                    content = "엄청 귀여운데 분리불안이 있어요.",
                    histories = listOf(
                        ForeverdogHistory("보호소 입소: 2023년 초"),
                        ForeverdogHistory("임시보호: 2023년 초"),
                        ForeverdogHistory("입양: 2023년 초, 임시 보호했던 봉사자분에게 입양"),
                    ),
                    searchKeyword = ""
                )
            ),
            Arguments.of(
                Foreverdog(
                    id = ObjectId.get(),
                    status = ForeverdogStatus.SHELTER_PROTECTION,
                    shelterId = ObjectId.get().toString(),
                    announcementId = UUID.randomUUID().toString(),
                    profileImageUrl = "https://profileimage.com",
                    gender = Gender.WOMAN,
                    isNeutering = false,
                    neuteringDate = null,
                    protectedStartDate = LocalDate.now().minusMonths(1),
                    name = "가을",
                    birthYear = null,
                    breed = null,
                    weight = null,
                    socializationLevel = SocializationLevel.GOOD,
                    content = "현재, 우리 쉼터에서 제일 오래있었어요.",
                    histories = listOf(
                        ForeverdogHistory("보호소 입소: 2022년 말"),
                    ),
                    searchKeyword = ""
                )
            ),
            Arguments.of(
                Foreverdog(
                    id = ObjectId.get(),
                    status = ForeverdogStatus.SHELTER_PROTECTION,
                    shelterId = ObjectId.get().toString(),
                    announcementId = UUID.randomUUID().toString(),
                    profileImageUrl = "https://profileimage.com",
                    gender = Gender.MAN,
                    isNeutering = true,
                    neuteringDate = LocalDate.now().minusMonths(8),
                    protectedStartDate = LocalDate.now().minusMonths(8),
                    name = "복돌",
                    birthYear = 2022,
                    breed = null,
                    weight = 18.0,
                    socializationLevel = SocializationLevel.GOOD,
                    content = "어릴때부터 쉼터에 있었어요. 피부가 고무줄같아요. 안에 사람이 들어있을지도..?",
                    histories = listOf(
                        ForeverdogHistory("보호소 입소: 2023년 초"),
                    ),
                    searchKeyword = ""
                )
            ),
        )
    }
}