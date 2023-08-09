package com.sheltersdog.core.log

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.MultiValueMap

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RequestLog(
    val id: String,
    val logType: String = "REQUEST",
    val headers: HttpHeaders,
    val logTime: Long = System.nanoTime(),
    val method: String,
    val path: String,
    val query: MultiValueMap<String, String>? = null,
    val body: String? = null,
)

data class ResponseLog(
    val id: String,
    val logType: String = "RESPONSE",
    val code: Int = HttpStatus.OK.value(),
    val headers: HttpHeaders,
    val logTime: Long = System.nanoTime(),
)

data class ResponseBodyLog(
    val id: String,
    val logType: String = "RESPONSE_BODY",
    val body: String,
)

data class TraceLog(
    val id: String,
    val logType: String = "TRACE",
    val traces: List<String>,
)
