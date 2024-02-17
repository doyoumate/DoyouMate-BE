package com.doyoumate.common.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.reactivestreams.Subscription
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.WebFilter
import reactor.core.CoreSubscriber
import reactor.core.publisher.Hooks
import reactor.core.publisher.Operators
import reactor.util.context.Context
import java.util.*
import java.util.stream.Collectors

@Configuration
class MdcConfiguration {
    private val mdcContextKey = MdcConfiguration::class.java.name

    @PostConstruct
    fun contextOperatorHook() {
        Hooks.onEachOperator(
            mdcContextKey, Operators.lift { _, subscriber -> MdcContextLifter(subscriber) })
    }

    @PreDestroy
    fun cleanupHook() {
        Hooks.resetOnEachOperator(mdcContextKey)
    }

    @Bean
    fun mdcFilter(): WebFilter =
        WebFilter { exchange, chain ->
            chain.filter(exchange)
                .contextWrite { it.put("traceId", UUID.randomUUID().toString().substring(0..7)) }
        }
}

class MdcContextLifter<T>(private val coreSubscriber: CoreSubscriber<T>) : CoreSubscriber<T> {
    override fun onSubscribe(subscription: Subscription) {
        coreSubscriber.onSubscribe(subscription)
    }

    override fun onNext(t: T) {
        synchronizeMdc(coreSubscriber.currentContext())
        coreSubscriber.onNext(t)
    }

    override fun onError(throwable: Throwable) {
        coreSubscriber.onError(throwable)
    }

    override fun onComplete() {
        coreSubscriber.onComplete()
    }

    override fun currentContext(): Context = coreSubscriber.currentContext()

    private fun synchronizeMdc(context: Context) {
        MDC.setContextMap(
            context.stream()
                .collect(
                    Collectors.toMap(
                        { it.key.toString() },
                        { it.value.toString() })
                )
        )
    }
}
