package com.doyoumate.common.util

import com.doyoumate.domain.fixture.createJwtAuthentication
import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKStubScope
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

infix fun <T> MockKStubScope<Mono<T>, *>.returns(value: T?): MockKAdditionalAnswerScope<Mono<T>, *> =
    returns(Mono.justOrEmpty(value))

infix fun <T> MockKStubScope<Flux<T>, *>.returns(values: Iterable<T>): MockKAdditionalAnswerScope<Flux<T>, *> =
    returns(Flux.fromIterable(values))

fun withMockUser() {
    SecurityContextHolder.getContext().authentication = createJwtAuthentication()
}

fun withMockAdmin() {
    SecurityContextHolder.getContext().authentication =
        createJwtAuthentication(authorities = setOf(SimpleGrantedAuthority("ADMIN")))
}
