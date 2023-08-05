package com.sheltersdog.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()
    val scope = CoroutineScope(Dispatchers.IO)

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .onEach { event ->
                coroutineContext.ensureActive()
                scope.launch {
                    onEvent(event)
                }
            }.launchIn(scope)
    }
}