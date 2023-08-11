package com.sheltersdog.core.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import reactor.core.publisher.Flux

class SheltersdogRequestLogDecorator(
    delegate: ServerHttpRequest,
    objectMapper: ObjectMapper,
    logId: String,
) : ServerHttpRequestDecorator(delegate) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private var body: Flux<DataBuffer>

    init {
        val contentType = delegate.headers.contentType ?: ""
        val flux = super.getBody()

        var copyBody: ByteArray? = null
        this.body = if (!legalLogMediaTypes.contains(contentType)) {
            flux
        } else {
            flux.map { dataBuffer ->
                val bodyPair = dataBufferWrapper(dataBuffer)
                copyBody = bodyPair.first
                bodyPair.second
            }
        }.doOnNext {
            log.info(
                objectMapper.writeValueAsString(
                    RequestLog(
                        id = logId,
                        headers = delegate.headers,
                        method = delegate.method.name(),
                        path = delegate.uri.path,
                        query = delegate.queryParams,
                        body = copyBody?.decodeToString(),
                    )
                )
            )
        }

    }

    override fun getBody(): Flux<DataBuffer> = body
}