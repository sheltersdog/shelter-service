package com.sheltersdog.core.mail

enum class SheltersdogMailType(
    val subject: String,
    val filename: String,
) {
    SHELTER_ADMIN_INVITE(
        "쉼터 관리자 초대 메일입니다.",
        "classpath:mail_template/shelter_admin_invite.html"
    ),

}
