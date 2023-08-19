package com.sheltersdog.core.exception

import org.springframework.http.HttpStatus

class SheltersdogException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val variables: Map<String, Any?>? = null,
    val exceptionType: ExceptionType,
    override val message: String = exceptionType.exceptionMessage().description,
) : RuntimeException() {
    constructor(
        exceptionType: ExceptionType,
        variables: Map<String, Any?> = mapOf(),
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ) : this(
        message = exceptionType.exceptionMessage().description,
        exceptionType = exceptionType,
        httpStatus = httpStatus,
        variables = variables,
    )
}