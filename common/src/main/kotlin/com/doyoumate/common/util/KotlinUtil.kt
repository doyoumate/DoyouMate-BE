package com.doyoumate.common.util

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T> Flux<T>.collectSet(): Mono<Set<T>> = collectList().map { it.toSet() }

fun <T> Flux<T>.collectHashSet(): Mono<HashSet<T>> = collectList().map { it.toHashSet() }
