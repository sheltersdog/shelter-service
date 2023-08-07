package com.sheltersdog.core.log

import org.springframework.http.MediaType

val legalLogMediaTypes = listOf(
    MediaType.TEXT_XML,
    MediaType.APPLICATION_XML,
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_JSON_UTF8,
    MediaType.TEXT_PLAIN,
    MediaType.TEXT_XML,
    MediaType.valueOf("text/plain;charset=UTF-8"),
)
