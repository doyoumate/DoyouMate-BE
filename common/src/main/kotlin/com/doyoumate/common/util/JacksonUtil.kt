package com.doyoumate.common.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

inline fun <reified T> JsonNode.getValue(key: String): T =
    get(key)
        .get("value")
        .toString()
        .replace("\"", "")
        .run {
            when(T::class) {
                String::class -> this
                Int::class -> toInt()
                Long::class -> toLong()
                Float::class -> toFloat()
                Double::class -> toDouble()
                else -> this
            } as T
        }

fun XmlMapper.getRows(xml: String): Flux<JsonNode> =
    readTree(xml)
        .findValue("data")
        ?.run { if (isArray) toFlux() else Flux.just(this) } ?: Flux.empty()

fun XmlMapper.getRow(xml: String): Mono<JsonNode> =
    readTree(xml)
        .findValue("data")
        ?.run { if (isArray) first() else this }
        .toMono()
