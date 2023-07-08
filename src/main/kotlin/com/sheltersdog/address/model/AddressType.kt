package com.sheltersdog.address.model

import com.sheltersdog.address.entity.Address
import kotlin.reflect.KProperty1

enum class AddressType {
    SIDO,
    SGG,
    UMD,
    RI,
}

fun AddressType.getParentPropertyCode():KProperty1<Address, Any?> = when (this) {
    AddressType.SIDO -> Address::regionCd
    AddressType.SGG -> Address::sidoCd
    AddressType.UMD -> Address::sggCd
    AddressType.RI -> Address::umdCd
}

fun AddressType.getParentPropertyName():KProperty1<Address, Any?> = when (this) {
    AddressType.SIDO -> Address::regionName
    AddressType.SGG -> Address::sidoName
    AddressType.UMD -> Address::sggName
    AddressType.RI -> Address::umdName
}

fun AddressType.getPropertyCode(): KProperty1<Address, Any?> = when (this) {
    AddressType.SIDO -> Address::sidoCd
    AddressType.SGG -> Address::sggCd
    AddressType.UMD -> Address::umdCd
    AddressType.RI -> Address::riCd
}

fun AddressType.getPropertyName(): KProperty1<Address, Any?> = when (this) {
    AddressType.SIDO -> Address::sidoName
    AddressType.SGG -> Address::sggName
    AddressType.UMD -> Address::umdName
    AddressType.RI -> Address::riName
}