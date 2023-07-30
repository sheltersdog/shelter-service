package com.sheltersdog.volunte.entity

import com.sheltersdog.address.entity.Address
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class CrawlingVolunteer(

    @Id
    val id: ObjectId? = null,
    val name: String,
    val shelterName: String,

    val isShort: Boolean = true,
    val categories: List<String> = listOf(),

    val addressRegionCode: Long,
    val detailAddress: String = "",

    val startDate: LocalDate,
    val endDate: LocalDate,
    val day: String,
    val time: String,
    val content: String,
    val url: String,

    var address: Address? = null,

    val searchKeyword: String = "",
)