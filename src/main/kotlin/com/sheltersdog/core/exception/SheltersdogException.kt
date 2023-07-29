package com.sheltersdog.core.exception

import org.springframework.http.HttpStatus

class SheltersdogException(
    override val message: String,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException() {

}