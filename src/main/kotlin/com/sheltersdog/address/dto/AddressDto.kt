package com.sheltersdog.address.dto

import com.sheltersdog.address.model.AddressType

data class AddressDto(
    val id: String,
    val type: AddressType,

    val regionName: String,
    val regionCode: Long,

    val name: String,
    val code: String,
)