package com.sheltersdog.core.log

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import java.util.UUID

class SheltersdogServerWebExchangeDecorator(delegate: ServerWebExchange) : ServerWebExchangeDecorator(delegate) {
    private var requestDecorator: SheltersdogRequestLogDecorator
    private var responseDecorator: SheltersdogResponseLogDecorator

    init {
        val logId = UUID.randomUUID().toString()
        requestDecorator = SheltersdogRequestLogDecorator(delegate = delegate.request, logId)
        responseDecorator = SheltersdogResponseLogDecorator(delegate = delegate.response, logId)
    }

    override fun getRequest(): ServerHttpRequest {
        return requestDecorator
    }

    override fun getResponse(): ServerHttpResponse {
        return responseDecorator
    }

}