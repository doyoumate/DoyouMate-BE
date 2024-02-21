package com.doyoumate.common.util

import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun RouterFunctionDsl.queryParams(vararg names: String): RequestPredicate =
    names.map { queryParam(it) { true } }
        .reduce { total, next -> total and next }

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
