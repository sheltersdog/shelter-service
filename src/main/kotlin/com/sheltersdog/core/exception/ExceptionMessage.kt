package com.sheltersdog.core.exception

enum class ExceptionMessage(val description: String) {
    SHELTERSDOG_EXCEPTION("서버 에러가 발생하였습니다."),

    NOT_EXIST_TOKEN("인증 토큰이 존재하지 않습니다."),
    WRONG_PATH("잘못된 경로로 요청하였습니다."),

    TOKEN_PARSE_EXCEPTION("토큰 검증에 실패하였습니다."),

    NOT_FOUND_ADDRESS("Address를 찾을 수 없습니다."),

    NOT_FOUND_KAKAO_DOCUMENT("카카오 주소 조회를 실패했습니다."),
    ;

}