package com.doyoumate.common.util

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuple4

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1

operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component1(): T1 = t1

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component2(): T2 = t2

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component3(): T3 = t3

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component4(): T4 = t4

fun <T> Flux<T>.collectSet(): Mono<Set<T>> = collectList().map { it.toSet() }
