package com.doyoumate.common.util

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKStubScope
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

infix fun <T> MockKStubScope<Mono<T>, *>.returns(value: T?): MockKAdditionalAnswerScope<Mono<T>, *> =
    returns(Mono.justOrEmpty(value))

infix fun <T> MockKStubScope<Flux<T>, *>.returns(values: Iterable<T>): MockKAdditionalAnswerScope<Flux<T>, *> =
    returns(Flux.fromIterable(values))

fun <T> empty(): Mono<T> = Mono.empty()
