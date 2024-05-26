package com.doyoumate.common.util

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun RouterFunctionDsl.queryParams(vararg names: String): RequestPredicate =
    names.map {
        queryParam(it) { true }
    }.reduce { total, next -> total and next }

inline fun <reified T> ServerRequest.getQueryParam(name: String): T? =
    this.queryParamOrNull(name)
        ?.run {
            when(T::class) {
                Int::class -> toInt()
                Long::class -> toLong()
                Float::class -> toFloat()
                Double::class -> toDouble()
                Boolean::class -> toBoolean()
                else -> this
            } as T
        }

fun ServerRequest.getPageable(): Pageable =
    PageRequest.of(getQueryParam("page")!!, getQueryParam("size")!!)

fun Flux<DataBuffer>.toByteArray(): Mono<ByteArray> =
    DataBufferUtils.join(this)
        .map {
            ByteArray(it.readableByteCount())
                .also(it::read)
                .apply { DataBufferUtils.release(it) }
        }
