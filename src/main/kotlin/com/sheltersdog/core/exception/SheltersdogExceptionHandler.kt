package com.sheltersdog.core.exception

import com.sheltersdog.core.properties.ActiveProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.MissingClaimException
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono
import java.io.PrintWriter
import java.io.StringWriter

@ControllerAdvice
class SheltersdogExceptionHandler @Autowired constructor(
    val activeProperties: ActiveProperties
) {
    val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Mono<ResponseEntity<Any>> {
        if (activeProperties.active != "prod") {
            val message = getPrintStackTrace(e)
            log.error(message)
        }

        return when (e) {
            is SheltersdogException -> handleSheltersdogException(e)
            is ValidationException -> handleValidationException(e)
            is DataIntegrityViolationException -> handleDataIntegrityViolationException(e)
            is ExpiredJwtException,
            is IncorrectClaimException,
            is MissingClaimException -> handleAuthorizationException(e)

            else -> Mono.just(
                ResponseEntity.internalServerError()
                    .body(e.message)
            )
        }
    }

    fun handleSheltersdogException(
        e: SheltersdogException
    ): Mono<ResponseEntity<Any>> {

        return Mono.just(
            ResponseEntity
                .status(e.httpStatus)
                .body(e.message)
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.badRequest()
                .body(e.message)
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.badRequest()
                .body(e.message)
        )
    }

    @ExceptionHandler(ExpiredJwtException::class, IncorrectClaimException::class, MissingClaimException::class)
    fun handleAuthorizationException(e: Exception): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.message)
        )
    }

    private fun getPrintStackTrace(e: Exception): String {
        val writer = StringWriter()
        e.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }
}