package com.sheltersdog.core.exception

class SheltersdogException(
    val type: ExceptionType,
    val variables: Map<String, Any?> = mapOf(),
) : RuntimeException()