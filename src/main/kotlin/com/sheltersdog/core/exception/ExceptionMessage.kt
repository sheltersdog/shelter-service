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
    ACCESS_DENIED("접근 권한이 없습니다."),

    NOT_FOUND_FOREVERDOG("강아지를 찾을 수 없습니다."),
    DB_UPDATE_FAIL("업데이트에 실패하였습니다."),

    NOT_FOUND_KAKAO_TOKEN("카카오 토큰을 조회하는데 실패하였습니다."),
    NOT_FOUND_KAKAO_USER_INFO("카카오 유저정보를 조회하는데 실패하였습니다."),

    FILE_TYPE_WRONG("파일 타입이 일치하지 않습니다."),

    KAKAO_LEAVE_MESSAGE("잘못된 않은 카카오계정 탈퇴 요청입니다."),

    ALREADY_JOIN_USER("이미 가입된 회원입니다."),
    NOT_FOUND_USER("존재하지 않는 회원입니다."),

    VOLUNTEER_NOT_FOUND("존재하지 않는 봉사정보입니다."),
    SHELTER_ADMIN_INVITE_FAIL("쉼터 관리자 초대에 실패했습니다.")
    ;

}