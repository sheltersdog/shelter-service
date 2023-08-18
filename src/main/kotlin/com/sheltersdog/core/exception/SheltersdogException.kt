package com.sheltersdog.core.exception

import com.sheltersdog.core.log.LogMessage
import com.sheltersdog.core.log.exceptionMessage
import org.springframework.http.HttpStatus

class SheltersdogException(
    override val message: String,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val variables: Map<String, Any?>? = null,
    val logMessage: LogMessage,
    val stackTraces: List<StackTraceElement>,
) : RuntimeException() {
    constructor(
        logMessage: LogMessage,
        variables: Map<String, Any?> = mapOf(),
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ) : this(
        message = logMessage.exceptionMessage().description,
        logMessage = logMessage,
        httpStatus = httpStatus,
        variables = variables,
        stackTraces = Thread.currentThread().stackTrace.toList(),
    )

}