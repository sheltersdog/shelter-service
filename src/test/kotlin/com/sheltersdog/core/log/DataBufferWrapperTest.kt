package com.sheltersdog.core.log

import io.netty.buffer.UnpooledByteBufAllocator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.io.buffer.NettyDataBufferFactory

class DataBufferWrapperTest {

    @MethodSource
    @ParameterizedTest(name = "actual: {0}, expected: {1}")
    fun dataBufferWrapperTest(
        actual: String, expected: String,
        test: (expected: Any, actual: Any) -> Unit,
    ) {
        val bytes = actual.encodeToByteArray()
        val nettyDataBufferFactory = NettyDataBufferFactory(
            UnpooledByteBufAllocator(false)
        )
        val progressDataBuffer = nettyDataBufferFactory.wrap(bytes)
        val actualPair = dataBufferWrapper(progressDataBuffer)

        val expectedBytes = expected.encodeToByteArray()
        val expectedDataBuffer = nettyDataBufferFactory.wrap(expectedBytes)

        test(expectedBytes.decodeToString(), actualPair.first.decodeToString())
        test(expectedDataBuffer, actualPair.second)
    }

    companion object {
        @JvmStatic
        fun dataBufferWrapperTest() = listOf(
            Arguments.of(
                "awefeaw", "gawefw",
                { expected: Any, actual: Any -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                "gawefw", "gawefw",
                { expected: Any, actual: Any -> assertEquals(expected, actual) }
            ),
            Arguments.of(
                "gawefw", "",
                { expected: Any, actual: Any -> assertNotEquals(expected, actual) }
            ),
            Arguments.of(
                "", "gawefw",
                { expected: Any, actual: Any -> assertNotEquals(expected, actual) }
            ),
        )
    }
}