package com.sheltersdog.core.log

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactivestreams.Subscription
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import reactor.core.CoreSubscriber
import reactor.core.publisher.Hooks
import reactor.core.publisher.Operators
import reactor.util.context.Context
import java.util.stream.Collectors


@Configuration
class MDCContextLifterConfig {
    companion object {
        val MDC_CONTEXT_REACTOR_KEY: String = MDCContextLifterConfig::class.java.name
    }

    @PostConstruct
    fun hook() {
        Hooks.onEachOperator(
            MDC_CONTEXT_REACTOR_KEY,
            Operators.lift { _, subscriber ->
                MDCContextLifter(subscriber)
            }
        )
    }

    @PreDestroy
    fun cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY)
    }
}


class MDCContextLifter<T>(private val coreSubscriber: CoreSubscriber<T>) : CoreSubscriber<T> {
    override fun onSubscribe(s: Subscription) = coreSubscriber.onSubscribe(s)

    override fun onError(t: Throwable?) = coreSubscriber.onError(t)

    override fun onComplete() = coreSubscriber.onComplete()

    override fun onNext(t: T) {
        coreSubscriber.currentContext().copyToMdc()
        coreSubscriber.onNext(t)
    }
}

private fun Context.copyToMdc() {
    if (!this.isEmpty) {
        val map: Map<String, String> = this.stream()
            .collect(Collectors.toMap({ e -> e.key.toString() }, { e -> e.value.toString() }))
        MDC.setContextMap(map)
    } else {
        MDC.clear()
    }
}

fun saveMdcTrace(trace: String = "") {
    val traceLog = trace.ifBlank {
        val action = Thread.currentThread().stackTrace[4]
        "${action.className}::${action.methodName}"
    }

    CoroutineScope(Dispatchers.IO).launch {
        val prevOrder = MDC.get("order") ?: "0"
        val order = prevOrder.toInt() + 1
        MDC.put(prevOrder, traceLog)
        MDC.put("order", order.toString())
    }
}
