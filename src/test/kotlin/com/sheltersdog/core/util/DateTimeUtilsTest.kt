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
    @ParameterizedTest(name = "date: {0}")
    fun yyyyMMddHHmmssToLocalDateThrowsTest(dateTime: String) {
        assertThrows(DateTimeParseException::class.java) { yyyyMMddHHmmssToLocalDate(dateTime) }
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

    @MethodSource
    @ParameterizedTest(name = "time: {0} :: expectedTime: {1}")
    fun HHmmssToLocalTimeTest(
        time: String,
        expectedTime: LocalTime,
        test: (expected: LocalTime, actual: LocalTime) -> Unit,
    ) {
        val actualTime = HHmmssToLocalTime(time)
        test(expectedTime, actualTime)
    }

    @MethodSource
    @ParameterizedTest(name = "time: {0}")
    fun HHmmssToLocalTimeThrowsTest(time: String) {
        assertThrows(DateTimeParseException::class.java) { HHmmssToLocalTime(time) }
    }

    @MethodSource
    @ParameterizedTest(name = "time: {0}")
    fun HHmmToLocalTimeTest(
        time: String,
        expectedTime: LocalTime,
        test: (expected: LocalTime, actual: LocalTime) -> Unit,
    ) {
        val actualTime = HHmmToLocalTime(time)
        test(expectedTime, actualTime)
    }

    @MethodSource
    @ParameterizedTest(name = "time: {0}")
    fun HHmmToLocalTimeThrowsTest(time: String) {
        assertThrows(DateTimeParseException::class.java) { HHmmToLocalTime(time) }
    }


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
        fun yyyyMMddHHmmssToLocalDateThrowsTest() = listOf(
            Arguments.of("2023-08-12 12:12:123"),
            Arguments.of("2023-08-12 12:112:12"),
            Arguments.of("2023-8-12 12:12:12"),
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

        @JvmStatic
        fun HHmmssToLocalTimeTest() = listOf(
            Arguments.of(
                "13:13:13", LocalTime.of(13, 13, 13),
                { expected: LocalTime, actual: LocalTime -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "11:11:11", LocalTime.of(13, 13, 13),
                { expected: LocalTime, actual: LocalTime -> assertNotEquals(expected, actual) }
            )
        )

        @JvmStatic
        fun HHmmssToLocalTimeThrowsTest() = listOf(
            Arguments.of("13:13"),
            Arguments.of("13:13:1"),
            Arguments.of("13:13:101"),
            Arguments.of("13:133"),
            Arguments.of("13"),
            Arguments.of("13:111"),
        )

        @JvmStatic
        fun HHmmToLocalTimeTest() = listOf(
            Arguments.of(
                "13:12", LocalTime.of(13, 12),
                { expected: LocalTime, actual: LocalTime -> assertEquals(expected, actual) },
            ),
            Arguments.of(
                "13:11", LocalTime.of(13, 12),
                { expected: LocalTime, actual: LocalTime -> assertNotEquals(expected, actual) },
            ),
        )

        @JvmStatic
        fun HHmmToLocalTimeThrowsTest() = listOf(
            Arguments.of("13:13:12"),
            Arguments.of("13:13:121"),
            Arguments.of("13"),
            Arguments.of("1"),
            Arguments.of("13:1312"),
            Arguments.of("13:13:1"),
        )
    }
}