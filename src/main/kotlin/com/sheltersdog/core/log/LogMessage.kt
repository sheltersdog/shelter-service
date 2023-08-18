package com.sheltersdog.core.log

import com.sheltersdog.core.exception.ExceptionMessage

enum class LogMessage(val description: String) {
    NOT_FOUND_ADDRESS("존재하지 않는 Address 데이터입니다. {}"),

    NOT_FOUND_KAKAO_DOCUMENT("카카오 주소 조회를 실패했습니다.. {}"),
    ;

}

fun LogMessage.exceptionMessage(): ExceptionMessage {
    ExceptionMessage.values().firstOrNull { exceptionMessage ->
        exceptionMessage.name == this.name
    }
    return ExceptionMessage.SHELTERSDOG_EXCEPTION
}