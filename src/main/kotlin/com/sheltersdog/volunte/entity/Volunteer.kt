package com.sheltersdog.volunte.entity

import com.sheltersdog.address.entity.Address
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.volunte.entity.model.SourceType
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.Range
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document
data class Volunteer (
    @Id
    val id: ObjectId? = null,
    val sourceType: SourceType = SourceType.SERVICE,

    val shelter: Shelter? = null,
    val shelterId: String? = null,
    val shelterName: String,

    val isShort: Boolean = true,
    val categories: List<String> = listOf(),

    val regionCode: Long,
    val address: Address? = null,
    val detailAddress: String? = null,
    val isPrivateDetailAddress: Boolean = true,

    val isAlwaysRecruiting: Boolean = false,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val days: List<String> = listOf(),

    val content: String? = null,
    val url: String? = null,

    @field:Range(min = 10_000_000_00, max = 1_00_000_000_00)
    val arriveRegionCode: Long? = null,
    val arriveDetailAddress: String? = null,

    val searchKeyword: String,
)
