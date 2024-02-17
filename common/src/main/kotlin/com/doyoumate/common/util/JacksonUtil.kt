package com.doyoumate.common.util

import com.fasterxml.jackson.databind.JsonNode

inline fun <reified T> JsonNode.getValue(key: String): T =
    get(key)
        .get("value")
        .toString()
        .replace("\"", "")
        .let {
            when (T::class) {
                Int::class -> it.toInt()
                Double::class -> it.toDouble()
                else -> it
            } as T
        }
