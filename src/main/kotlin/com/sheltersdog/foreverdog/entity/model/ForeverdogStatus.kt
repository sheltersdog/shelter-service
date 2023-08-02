package com.sheltersdog.foreverdog.entity.model

enum class ForeverdogStatus(description: String) {
    SHELTER_PROTECTION("보호중이에요"),
    TEMPORARY_PROTECTION("임시 보호 중이에요."),
    ADOPT("입양갔어요"),
    PRIVATE("비공개 (안락사 / 자연사 / 기타)"),
}