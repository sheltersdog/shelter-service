package com.sheltersdog.core.log

import io.netty.buffer.UnpooledByteBufAllocator
import org.apache.commons.io.IOUtils.toByteArray
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.NettyDataBufferFactory

fun <T : DataBuffer> dataBufferWrapper(buffer: T): Pair<ByteArray, DataBuffer> {
    val dataBuffer = buffer.asInputStream()
    val bytes = toByteArray(dataBuffer)

    val nettyDataBufferFactory = NettyDataBufferFactory(
        UnpooledByteBufAllocator(false)
    )

    DataBufferUtils.release(buffer)
    return Pair(bytes, nettyDataBufferFactory.wrap(bytes))
}
