package com.sheltersdog.core.util

import com.sheltersdog.core.exception.ExceptionMessage
import com.sheltersdog.core.log.LogMessage
import com.sheltersdog.core.log.loggingAndException
import com.sheltersdog.core.log.validLoggingAndException
import java.security.Principal

fun Map<String, Any>.notStringThrow(name: String): String {
    if (this[name] != null && this[name].toString().isNotBlank()) {
        return this[name].toString()
    }

    throw LogMessage.VALID_CHECK_WRONG.validLoggingAndException(
        variables = mapOf(name to this[name]),
        parameterName = name,
        exceptionMessage = "$name ${ExceptionMessage.VALID_CHECK_WRONG.description}"
    )
}

fun Principal.nameNotStringThrow(): String {
    if (!this.name.isNullOrBlank()) return this.name.toString()

    throw LogMessage.ACCESS_TOKEN_WRONG.loggingAndException(
        variables = mapOf("principal" to this)
    )
}
