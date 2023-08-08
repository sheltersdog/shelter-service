package com.sheltersdog.core.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.single

class SheltersdogResponseLogDecorator(
    delegate: ServerHttpResponse,
    private val objectMapper: ObjectMapper,
    private val logId: String,
) :
    ServerHttpResponseDecorator(delegate) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private var copyBody: ByteArray? = null

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        val contentType = delegate.headers.contentType
        if (!legalLogMediaTypes.contains(contentType)) {
            return logging(super.writeWith(body))
        }

        if (body is Mono) {
            val pair = body.publishOn(single()).map { dataBuffer -> dataBufferWrapper(dataBuffer) }
            val copyBuffer = pair.map { p ->
                copyBody = p.first
                p.second
            }

            return logging(super.writeWith(copyBuffer))
        } else if (body is Flux) {
            val pair = body.publishOn(single()).map { dataBuffer -> dataBufferWrapper(dataBuffer) }
            val copyBuffer = pair.map { p ->
                copyBody = p.first
                p.second
            }

            return logging(super.writeWith(copyBuffer))
        }

        return logging(super.writeWith(body))
    }

    private fun logging(publisher: Mono<Void>): Mono<Void> {
        return publisher.doFinally {
            log.info(
                objectMapper.writeValueAsString(
                    ResponseLog(
                        id = logId,
                        headers = headers,
                        code = delegate.statusCode?.value() ?: -1,
                        body = copyBody?.decodeToString(),
                    )
                )
            )
        }
    }

}