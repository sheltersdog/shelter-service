package com.sheltersdog.address.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.entity.Address
import com.sheltersdog.address.model.AddressType

fun addressToDto(
    entity: Address,
): AddressDto {
    val name = when (entity.type) {
        AddressType.SIDO -> entity.sidoName
        AddressType.SGG -> entity.sggName
        AddressType.UMD -> entity.umdName
        AddressType.RI -> entity.riName
    }
    val code = when (entity.type) {
        AddressType.SIDO -> entity.sidoCd
        AddressType.SGG -> entity.sggCd
        AddressType.UMD -> entity.umdCd
        AddressType.RI -> entity.riCd
    }

    return AddressDto(
        id = entity.id.toString(),
        type = entity.type,
        regionName = entity.regionName,
        regionCode = entity.regionCd,
        name = name!!,
        code = code!!,
    )
}