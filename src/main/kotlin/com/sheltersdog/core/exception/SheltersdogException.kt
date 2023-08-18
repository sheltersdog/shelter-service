package com.sheltersdog.core.exception

import org.springframework.http.HttpStatus

class SheltersdogException(
    override val message: String,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException() {
    constructor(message: ExceptionMessage, httpStatus: HttpStatus = HttpStatus.BAD_REQUEST) : this(
        message = message.description,
        httpStatus = httpStatus,
    )

}