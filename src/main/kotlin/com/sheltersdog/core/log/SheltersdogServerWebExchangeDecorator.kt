package com.sheltersdog.core.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import java.util.UUID

class SheltersdogServerWebExchangeDecorator(delegate: ServerWebExchange, objectMapper: ObjectMapper) : ServerWebExchangeDecorator(delegate) {
    private var requestDecorator: SheltersdogRequestLogDecorator
    private var responseDecorator: SheltersdogResponseLogDecorator

    init {
        val logId = UUID.randomUUID().toString()
        requestDecorator = SheltersdogRequestLogDecorator(
            delegate = delegate.request,
            objectMapper = objectMapper,
            logId = logId
        )
        responseDecorator = SheltersdogResponseLogDecorator(
            delegate = delegate.response,
            objectMapper = objectMapper,
            logId = logId
        )
    }

    override fun getRequest(): ServerHttpRequest = requestDecorator
    override fun getResponse(): ServerHttpResponse = responseDecorator

}