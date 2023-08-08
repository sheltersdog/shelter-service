package com.sheltersdog.core.log

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.MultiValueMap

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RequestLog(
    val id: String,
    val headers: HttpHeaders,
    val logType: String = "REQUEST",
    val logTime: Long = System.nanoTime(),
    val method: String,
    val path: String,
    val query: MultiValueMap<String, String>? = null,
    val body: String? = null,
    )

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseLog(
    val id: String,
    val code: Int = HttpStatus.OK.value(),
    val headers: HttpHeaders,
    val logType: String = "RESPONSE",
    val logTime: Long = System.nanoTime(),
    val body: String? = null,
)
