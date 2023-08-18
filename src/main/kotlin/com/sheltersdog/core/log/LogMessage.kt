package com.sheltersdog.core.log

import com.sheltersdog.core.exception.ExceptionMessage
import com.sheltersdog.core.exception.SheltersdogException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

enum class LogMessage(private val description: String) {
    NOT_FOUND_ADDRESS("존재하지 않는 Address 데이터입니다. {}"),

    NOT_FOUND_KAKAO_DOCUMENT("카카오 주소 조회를 실패했습니다. {}"),
    NOT_FOUND_SHELTER("존재하지 않는 Shelter 데이터입니다. {}"),
    VALID_CHECK_WRONG("{}값이 존재하지 않거나 올바르지 않습니다. {}"),

    ACCESS_TOKEN_WRONG("토큰 정보가 올바르지 않습니다. {}"),
    ACCESS_DENIED("접근 권한이 없습니다. {}"),
    ;

    fun print(stackTrace: StackTraceElement): String {
        return "${stackTrace.fileName}:${stackTrace.lineNumber} :: ${this.description}"
    }

}

fun LogMessage.exceptionMessage(): ExceptionMessage {
    return ExceptionMessage.values().firstOrNull { exceptionMessage ->
        exceptionMessage.name == this.name
    } ?: ExceptionMessage.SHELTERSDOG_EXCEPTION
}

fun LogMessage.loggingAndException(
    staceTraceElement: StackTraceElement? = null,
    logger: Logger? = null,
    variables: Map<String, Any?>,
    exceptionMessage: String? = null,
): SheltersdogException {
    val stackTrace = staceTraceElement ?: Thread.currentThread().stackTrace[3]
    val log = logger ?: LoggerFactory.getLogger(stackTrace.className)
    log.debug(this.print(stackTrace), variables.toString())
    val message = exceptionMessage ?: this.exceptionMessage().description
    return SheltersdogException(
        message = message,
        httpStatus = HttpStatus.BAD_REQUEST
    )
}

fun LogMessage.validLoggingAndException(
    staceTraceElement: StackTraceElement? = null,
    logger: Logger? = null,
    variables: Map<String, Any?>,
    parameterName: String,
    exceptionMessage: String? = null,
): SheltersdogException {
    val stackTrace = staceTraceElement ?: Thread.currentThread().stackTrace[3]
    val log = logger ?: LoggerFactory.getLogger(stackTrace.className)
    log.debug(this.print(stackTrace), parameterName, variables.toString())
    val message = exceptionMessage ?: this.exceptionMessage().description

    return SheltersdogException(
        message = message,
        httpStatus = HttpStatus.BAD_REQUEST
    )
}