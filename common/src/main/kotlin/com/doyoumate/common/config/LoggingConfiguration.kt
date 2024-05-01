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
class LoggingConfiguration {
    private val key = LoggingConfiguration::class.java.name

    @PostConstruct
    fun contextOperatorHook() {
        Hooks.onEachOperator(key, Operators.lift { _, subscriber -> MdcContextLifter(subscriber) })
    }

    @PreDestroy
    fun cleanupHook() {
        Hooks.resetOnEachOperator(key)
    }

    @Order(Int.MIN_VALUE)
    @Bean
    fun mdcFilter(): MdcFilter = MdcFilter()

    @Order(Int.MIN_VALUE + 1)
    @Bean
    fun loggingFilter(): LoggingFilter = LoggingFilter()
}
