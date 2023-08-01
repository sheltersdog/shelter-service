package com.sheltersdog.address.mapper

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.entity.Address
import com.sheltersdog.address.model.AddressType

fun addressToDto(
    entity: Address,
): AddressDto {
    val name =
        if (entity.type == AddressType.SIDO) entity.sidoName
        else if (entity.type == AddressType.SGG) entity.sggName
        else if (entity.type == AddressType.UMD) entity.umdName
        else if (entity.type == AddressType.RI) entity.riName
        else ""
    val code =
        if (entity.type == AddressType.SIDO) entity.sidoCd
        else if (entity.type == AddressType.SGG) entity.sggCd
        else if (entity.type == AddressType.UMD) entity.umdCd
        else if (entity.type == AddressType.RI) entity.riCd
        else ""

    return AddressDto(
        id = entity.id.toString(),
        type = entity.type,
        regionName = entity.regionName,
        regionCode = entity.regionCd,
        name = name!!,
        code = code!!,
    )
}