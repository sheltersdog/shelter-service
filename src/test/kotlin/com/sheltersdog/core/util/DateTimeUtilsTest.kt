package com.sheltersdog.core.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException

class DateTimeUtilsTest {

    @MethodSource
    @ParameterizedTest(name = "date: {0} :: expectedDate: {1}")
    fun yyyyMMddToLocalDateTest(
        date: String,
        expectedDate: LocalDate,
        test: (expected: LocalDate, actual: LocalDate) -> Unit,
    ) {
        test(expectedDate, yyyyMMddToLocalDate(date))
    }

    @MethodSource
    @ParameterizedTest(name = "date: {0}")
    fun yyyyMMddToLocalDateThrowsTest(date: String) {
        assertThrows(DateTimeParseException::class.java) { yyyyMMddToLocalDate(date) }
    }

    @MethodSource
    @ParameterizedTest(name = "date: {0} :: expectedDate: {1}")
    fun localDateToKoreanFormatTest(
        date: LocalDate,
        expectedDate: String,
        test: (expected: String, actual: String) -> Unit,
    ) {
        val actualDate = localDateToKoreanFormat(date)
        test(expectedDate, actualDate)
    }

    @MethodSource
    @ParameterizedTest(name = "date: {0} :: expectedDate: {1}")
    fun yyyyMMddHHmmssToLocalDateTest(
        dateTime: String,
        expectedDate: LocalDateTime,
        test: (expected: LocalDateTime, actual: LocalDateTime) -> Unit,
    ) {
        val actualDate = yyyyMMddHHmmssToLocalDate(dateTime)
        test(expectedDate, actualDate)
    }

    @MethodSource
    @ParameterizedTest(name = "date: {0} :: isIncludeSecond: {1} expectedDate: {2}")
    fun localTimeKoreanFormatTest(
        time: LocalTime,
        isIncludeSecond: Boolean,
        expectedTime: String,
        test: (expected: String, actual: String) -> Unit,
    ) {
        val actualTime = localTimeKoreanFormat(time = time, isIncludeSecond = isIncludeSecond)
        test(expectedTime, actualTime)
    }
//    @Test
//    fun HHmmssToLocalTime() {
//    }
//
//    @Test
//    fun HHmmToLocalTime() {
//    }


    companion object {
        @JvmStatic
        fun yyyyMMddToLocalDateTest() = listOf(
            Arguments.of(
                "2023-08-12",
                LocalDate.of(2023, 8, 12),
                { expected: LocalDate, actual: LocalDate -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023.08.12", LocalDate.of(2023, 8, 12),
                { expected: LocalDate, actual: LocalDate -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023/08/12", LocalDate.of(2023, 8, 12),
                { expected: LocalDate, actual: LocalDate -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023.01.11", LocalDate.of(2023, 8, 11),
                { expected: LocalDate, actual: LocalDate -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                "2023.07.11", LocalDate.of(2023, 8, 11),
                { expected: LocalDate, actual: LocalDate -> assertNotEquals(expected, actual) }
            ),
        )

        @JvmStatic
        fun yyyyMMddToLocalDateThrowsTest(): List<Arguments> = listOf(
            Arguments.of("2023.01"),
            Arguments.of("2023.1.1"),
            Arguments.of("2023.12.3"),
            Arguments.of("null"),
            Arguments.of(""),
            Arguments.of("it is null"),
        )

        @JvmStatic
        fun localDateToKoreanFormatTest() = listOf(
            Arguments.of(
                LocalDate.of(2023, 8, 11),
                "2023년 08월 11일",
                { expected: String, actual: String -> assertEquals(expected, actual) }
            ), Arguments.of(
                LocalDate.of(2023, 7, 11),
                "2023년 08월 11일",
                { expected: String, actual: String -> assertNotEquals(expected, actual) }
            )
        )

        @JvmStatic
        fun yyyyMMddHHmmssToLocalDateTest() = listOf(
            Arguments.of(
                "2023-08-12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023-01-12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                "2023/08/12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023/01/12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                "2023.08.12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "2023.01.12 12:12:12",
                LocalDateTime.of(2023, 8, 12, 12, 12, 12),
                { expected: LocalDateTime, actual: LocalDateTime -> assertNotEquals(expected, actual) }
            ),
        )

        @JvmStatic
        fun localTimeKoreanFormatTest(): List<Arguments> = listOf(
            Arguments.of(
                LocalTime.of(12, 12),
                false,
                "12시 12분",
                { expected: String, actual: String -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                LocalTime.of(12, 12),
                true,
                "12시 12분",
                { expected: String, actual: String -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                LocalTime.of(12, 12, 12),
                true,
                "12시 12분 12초",
                { expected: String, actual: String -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                LocalTime.of(12, 12, 12),
                false,
                "13시 13분 12초",
                { expected: String, actual: String -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                LocalTime.of(13, 13),
                false,
                "13시 13분 12초",
                { expected: String, actual: String -> assertNotEquals(expected, actual) }
            ),
        )
    }
}