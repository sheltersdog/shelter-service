package com.sheltersdog.core.exception

import com.sheltersdog.core.properties.ActiveProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.MissingClaimException
import jakarta.validation.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter

@ControllerAdvice
class SheltersdogExceptionHandler @Autowired constructor(
    val activeProperties: ActiveProperties,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    suspend fun handleException(e: Exception): ResponseEntity<Any> {
        if (activeProperties.active != "prod" && e !is SheltersdogException) {
            val message = getPrintStackTrace(e)
            log.error(message)
        }

        return when (e) {
            is SheltersdogException -> handleSheltersdogException(e)
            is ValidationException -> handleValidationException(e)
            is DataIntegrityViolationException -> handleDataIntegrityViolationException(e)
            is ExpiredJwtException,
            is IncorrectClaimException,
            is MissingClaimException,
            -> handleAuthorizationException(e)

            else -> ResponseEntity.internalServerError()
                .body(e.message ?: "에러가 발생하였습니다.")
        }
    }

    suspend fun handleSheltersdogException(
        e: SheltersdogException,
    ): ResponseEntity<Any> {
        log.debug(e.logMessage.print(stackTrace = e.stackTraces), e.variables)
        return ResponseEntity.status(e.httpStatus).body(e.message)
    }

    fun handleValidationException(e: ValidationException): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(e.message ?: "에러가 발생하였습니다.")
    }

    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(e.message ?: "에러가 발생하였습니다.")
    }

    fun handleAuthorizationException(e: Exception): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message ?: "에러가 발생하였습니다.")
    }

    private fun getPrintStackTrace(e: Exception): String {
        val writer = StringWriter()
        e.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }
}