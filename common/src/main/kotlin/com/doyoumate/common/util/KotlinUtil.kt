package com.doyoumate.common.util

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuple3

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1

operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component1(): T1 = t1

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component2(): T2 = t2

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component3(): T3 = t3

fun <T, R> Mono<T>.mapOrEmpty(mapper: (T) -> R?): Mono<R> = flatMap { Mono.justOrEmpty(mapper(it)) }

fun <T, R> Flux<T>.mapOrEmpty(mapper: (T) -> R?): Flux<R> = flatMap { Mono.justOrEmpty(mapper(it)) }
