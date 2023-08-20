package com.sheltersdog.core.exception

import com.sheltersdog.core.log.LogMessage

enum class ExceptionType {
    NOT_FOUND_ADDRESS,

    NOT_FOUND_KAKAO_DOCUMENT,
    NOT_FOUND_SHELTER,
    VALID_CHECK_WRONG,

    ACCESS_TOKEN_WRONG,
    ACCESS_DENIED,

    NOT_FOUND_FOREVERDOG,
    DB_UPDATE_FAIL,

    NOT_FOUND_KAKAO_TOKEN,
    NOT_FOUND_KAKAO_USER_INFO,

    FILE_TYPE_WRONG,

    KAKAO_LEAVE_MESSAGE,

    ALREADY_JOIN_USER,
    NOT_FOUND_USER,

    VOLUNTEER_NOT_FOUND,
    SHELTER_ADMIN_INVITE_FAIL,
}

fun ExceptionType.logMessage(): LogMessage {
    return LogMessage.values().first { logMessage ->
        logMessage.name == this.name
    }
}

fun ExceptionType.exceptionMessage(): ExceptionMessage {
    return ExceptionMessage.values().first { exceptionMessage ->
        exceptionMessage.name == this.name
    }
}