package com.sheltersdog.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import reactor.core.publisher.Flux
import java.util.stream.Collectors

class SheltersdogRequestLogDecorator(delegate: ServerHttpRequest, private val logId: String) : ServerHttpRequestDecorator(delegate) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private var body: Flux<DataBuffer>

    init {
        val method = delegate.method.name()
        val contentType = delegate.headers.contentType ?: ""
        val flux = super.getBody()

        var copyBody = ""
        this.body = if (!legalLogMediaTypes.contains(contentType)) {
            flux
        } else {
            flux.map { dataBuffer ->
                val bodyPair = dataBufferWrapper(dataBuffer)
                copyBody = ""","body":${bodyPair.first.decodeToString()}""".trimIndent()
                bodyPair.second
            }
        }.doFinally {
            log.info(
                """{"logId":"$logId","LogType":"Request", "logTime":${System.nanoTime()}, "HttpMethod": "$method", "path": "${delegate.uri.path}"${
                    query(
                        delegate.uri.query
                    )
                }, "headers": [${headers(delegate.headers)}]$copyBody}""".trimIndent()
            )
        }
    }

    private fun headers(headers: HttpHeaders): String? {
        return headers.entries.stream().map { entry ->
            """{"${entry.key}":${entry.value}}""".trimIndent()
        }.collect(Collectors.joining(","))
    }

    private fun query(query: String?): String {
        if (query == null || query.isNotBlank()) return ""

        val queryArray = query.split("&").stream()
            .map { str ->
                val obj = str.split("=")
                """{"${obj[0]}","${obj[1]}"}"""
            }.collect(Collectors.joining(","))

        return """, "query": [$queryArray]"""
    }

    override fun getBody(): Flux<DataBuffer> {
        return body
    }


}