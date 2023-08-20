package com.sheltersdog.shelter.dto.request

data class GetShelterListRequest(
    val page: Int = 0,
    val size: Int = 10,
    val keyword: String = "",
    val regionCode: Long = 0,
    val isVolunteerRecruiting: Boolean? = null,
    val isDonationPossible: Boolean? = null,
)
