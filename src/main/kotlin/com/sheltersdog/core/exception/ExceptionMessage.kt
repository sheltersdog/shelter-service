package com.sheltersdog.core.exception

enum class ExceptionMessage(val description: String) {
    NOT_EXIST_TOKEN("인증 토큰이 존재하지 않습니다."),
    WRONG_PATH("잘못된 경로로 요청하였습니다."),

    TOKEN_PARSE_EXCEPTION("토큰 검증에 실패하였습니다."),
}