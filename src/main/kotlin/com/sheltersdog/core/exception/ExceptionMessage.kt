package com.sheltersdog.core.exception

enum class ExceptionMessage(val description: String) {
    SHELTERSDOG_EXCEPTION("에러가 발생하였습니다."),

    NOT_EXIST_TOKEN("인증 토큰이 존재하지 않습니다."),
    WRONG_PATH("잘못된 경로로 요청하였습니다."),

    TOKEN_PARSE_EXCEPTION("토큰 검증에 실패하였습니다."),
    ACCESS_TOKEN_WRONG("토큰 정보가 올바르지 않습니다."),

    NOT_FOUND_ADDRESS("주소를 찾을 수 없습니다."),

    NOT_FOUND_KAKAO_DOCUMENT("카카오 주소 조회를 실패했습니다."),
    NOT_FOUND_SHELTER("보호소나 쉼터를 찾을 수 없습니다."),

    VALID_CHECK_WRONG("값이 존재하지 않거나 올바르지 않습니다."),

    ;

}