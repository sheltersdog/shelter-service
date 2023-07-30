package com.sheltersdog.shelter.entity.model

enum class ShelterAuthority(val description: String) {
    ADMIN("최고 관리자"),
    ADMIN_MANAGE("관리자 관리"),
    VOLUNTEER_MANAGE("봉사 관리"),
    GALLERY_MANAGE("갤러리 관리"),
    DOG_MANAGE("아이들 관리"),
    SHELTER_DETAIL_MANAGE("쉼터 & 보호소 정보 수정"),

    GALLERY_VIEW("갤러리 뷰어"),
    DETAIL_VIEW("쉼터 & 보호소 상세 뷰어"),
    ;
}