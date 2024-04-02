package com.doyoumate.common.config

import com.doyoumate.common.logging.LoggingFilter
import com.doyoumate.common.logging.MdcContextLifter
import com.doyoumate.common.logging.MdcFilter
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import reactor.core.publisher.Hooks
import reactor.core.publisher.Operators

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

    @Order(Int.MIN_VALUE)
    @Bean
    fun mdcFilter(): MdcFilter = MdcFilter()

    @Bean
    fun loggingFilter(): LoggingFilter = LoggingFilter()
}
