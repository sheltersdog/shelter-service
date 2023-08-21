package com.sheltersdog.core.exception

import com.sheltersdog.core.exception.ExceptionType.*
import org.springframework.http.HttpStatus

enum class ExceptionType(
    val log: String,
    val message: String,
) {
    NOT_FOUND_ADDRESS(
        log = "존재하지 않는 Address 데이터입니다. {}\n{}",
        message = "주소를 찾을 수 없습니다.",
    ),

    NOT_FOUND_KAKAO_DOCUMENT(
        log = "카카오 주소 조회를 실패했습니다. {}\n{}",
        message = "카카오 주소 조회를 실패했습니다.",
    ),
    NOT_FOUND_SHELTER(
        log = "존재하지 않는 Shelter 데이터입니다. {}\n{}",
        message = "보호소나 쉼터를 찾을 수 없습니다.",
    ),
    VALID_CHECK_WRONG(
        log = "값이 존재하지 않거나 올바르지 않습니다. {}\n{}",
        message = "값이 존재하지 않거나 올바르지 않습니다.",
    ),
    TOKEN_PARSE_EXCEPTION(
        log = "토큰 검증에 실패하였습니다. {}\n{}",
        message = "토큰 검증에 실패하였습니다."
    ),

    ACCESS_TOKEN_WRONG(
        log = "토큰 정보가 올바르지 않습니다. {}\n{}",
        message = "토큰 정보가 올바르지 않습니다.",
    ),
    ACCESS_DENIED(
        log = "접근 권한이 없습니다. {}\n{}",
        message = "접근 권한이 없습니다.",
    ),
    NOT_FOUND_TOKEN(
        log = "토큰이 존재하지 않습니다. {}\n{}",
        message = "토큰 정보가 없습니다."
    ),

    NOT_FOUND_FOREVERDOG(
        log = "존재하지 않는 Foreverdog 데이터입니다. {}\n{}",
        message = "강아지를 찾을 수 없습니다.",
    ),
    DB_UPDATE_FAIL(
        log = "데이터베이스 업데이트에 실패하였습니다. {}\n{}",
        message = "업데이트에 실패하였습니다.",
    ),

    NOT_FOUND_KAKAO_TOKEN(
        log = "카카오 토큰을 조회하는데 실패하였습니다. {}\n{}",
        message = "카카오 토큰을 조회하는데 실패하였습니다.",
    ),
    NOT_FOUND_KAKAO_USER_INFO(
        log = "카카오 유저정보를 조회하는데 실패하였습니다. {}\n{}",
        message = "카카오 유저정보를 조회하는데 실패하였습니다.",
    ),

    FILE_TYPE_WRONG(
        log = "파일 타입이 일치하지 않습니다. {}\n{}",
        message = "파일 타입이 일치하지 않습니다.",
    ),

    KAKAO_LEAVE(
        log = "올바르지 않은 카카오 탈퇴 요청입니다. {}\n{}",
        message = "잘못된 않은 카카오계정 탈퇴 요청입니다.",
    ),

    ALREADY_JOIN_USER(
        log = "이미 가입된 회원입니다. {}\n{}",
        message = "이미 가입된 회원입니다.",
    ),
    NOT_FOUND_USER(
        log = "존재하지 않는 회원입니다. {}\n{}",
        message = "존재하지 않는 회원입니다.",
    ),

    VOLUNTEER_NOT_FOUND(
        log = "존재하지 않는 봉사 정보입니다. {}\n{}",
        message = "존재하지 않는 봉사정보입니다.",
    ),
    SHELTER_ADMIN_INVITE_FAIL(
        log = "쉼터 관리자 초대에 실패하였습니다. {}\n{}",
        message = "쉼터 관리자 초대에 실패했습니다.",
    ),

    WRONG_PATH(
        log = "잘못된 경로로 요청하였습니다. {}\n{}",
        message = "잘못된 경로로 요청하였습니다."
    ),
}

fun ExceptionType.httpStatus(): HttpStatus {
    return when (this) {
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

        KAKAO_LEAVE,

        ALREADY_JOIN_USER,
        NOT_FOUND_USER,

        VOLUNTEER_NOT_FOUND,
        SHELTER_ADMIN_INVITE_FAIL,

        TOKEN_PARSE_EXCEPTION,
        NOT_FOUND_TOKEN,
        WRONG_PATH,
        -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}