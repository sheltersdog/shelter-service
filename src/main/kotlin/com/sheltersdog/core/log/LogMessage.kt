package com.sheltersdog.core.log

import com.sheltersdog.core.exception.ExceptionMessage

enum class LogMessage(private val description: String) {
    NOT_FOUND_ADDRESS("존재하지 않는 Address 데이터입니다. {}"),

    NOT_FOUND_KAKAO_DOCUMENT("카카오 주소 조회를 실패했습니다. {}"),
    NOT_FOUND_SHELTER("존재하지 않는 Shelter 데이터입니다. {}"),
    VALID_CHECK_WRONG("값이 존재하지 않거나 올바르지 않습니다. {}"),

    ACCESS_TOKEN_WRONG("토큰 정보가 올바르지 않습니다. {}"),
    ACCESS_DENIED("접근 권한이 없습니다. {}"),

    NOT_FOUND_FOREVERDOG("존재하지 않는 Foreverdog 데이터입니다. {}"),
    DB_UPDATE_FAIL("데이터베이스 업데이트에 실패하였습니다. {}"),

    NOT_FOUND_KAKAO_TOKEN("카카오 토큰을 조회하는데 실패하였습니다. {}"),
    NOT_FOUND_KAKAO_USER_INFO("카카오 유저정보를 조회하는데 실패하였습니다. {}"),

    FILE_TYPE_WRONG("파일 타입이 일치하지 않습니다. {}"),

    KAKAO_LEAVE_MESSAGE("올바르지 않은 카카오 탈퇴 요청입니다. {}"),

    ALREADY_JOIN_USER("이미 가입된 회원입니다. {}"),
    NOT_FOUND_USER("존재하지 않는 회원입니다. {}"),

    VOLUNTEER_NOT_FOUND("존재하지 않는 봉사 정보입니다. {}"),
    ;

    fun print(stackTrace: List<StackTraceElement> = Thread.currentThread().stackTrace.toList()): String {
        val stackTraces =  stackTrace.filter { stackTraceElement ->
            val name = stackTraceElement.toString()
            name.startsWith("com.sheltersdog")
                    && !name.contains(MDCContextLifter::class.java.name)
                    && !name.contains(this::class.java.name)
        }

        return "\n\t\t${stackTraces.joinToString("\n\t\t") { trace -> "$trace" }} :: ${this.description}"
    }
}

fun LogMessage.exceptionMessage(): ExceptionMessage {
    return ExceptionMessage.values().firstOrNull { exceptionMessage ->
        exceptionMessage.name == this.name
    } ?: ExceptionMessage.SHELTERSDOG_EXCEPTION
}
