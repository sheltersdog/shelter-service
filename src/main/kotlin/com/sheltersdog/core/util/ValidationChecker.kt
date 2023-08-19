package com.sheltersdog.core.util

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import java.security.Principal

fun Map<String, Any>.notStringThrow(name: String): String {
    if (this[name] != null && this[name].toString().isNotBlank()) {
        return this[name].toString()
    }

    throw SheltersdogException(
        exceptionType = ExceptionType.VALID_CHECK_WRONG,
        variables = mapOf(name to this[name]),
    )
}

fun Principal.nameNotStringThrow(): String {
    if (!this.name.isNullOrBlank()) return this.name.toString()

    throw SheltersdogException(
        exceptionType = ExceptionType.ACCESS_TOKEN_WRONG,
        variables = mapOf("principal" to this),
    )
}
