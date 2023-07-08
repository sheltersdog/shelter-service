package com.sheltersdog.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val yyyyMMddDash = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val yyyyMMddSlash = DateTimeFormatter.ofPattern("yyyy/MM/dd")
private val yyyyMMddDot = DateTimeFormatter.ofPattern("yyyy.MM.dd")
private val yyyyMMddHHmmssDash = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val yyyyMMddHHmmssSlash = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
private val yyyyMMddHHmmssDot = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")

private val localDateKoreanFormat = DateTimeFormatter.ofPattern("yy년 MM월 dd일")

fun yyyyMMddToLocalDate(date: String): LocalDate {
    if (date.contains("-")) return LocalDate.parse(date, yyyyMMddDash)
    else if (date.contains("/")) return LocalDate.parse(date, yyyyMMddSlash)
    else if (date.contains(".")) return LocalDate.parse(date, yyyyMMddDot)

    return LocalDate.now()
}

fun localDateToKoreanFormat(date: LocalDate): String {
    return date.format(localDateKoreanFormat)
}

fun yyyyMMddHHmmssToLocalDate(date: String): LocalDateTime {
    if (date.contains("-")) return LocalDateTime.parse(date, yyyyMMddHHmmssDash)
    else if (date.contains("/")) return LocalDateTime.parse(date, yyyyMMddHHmmssSlash)
    else if (date.contains(".")) return LocalDateTime.parse(date, yyyyMMddHHmmssDot)

    return LocalDateTime.now()
}