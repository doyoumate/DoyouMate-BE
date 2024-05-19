package com.doyoumate.common.util

import mu.KLogger
import mu.KotlinLogging
import kotlin.reflect.jvm.jvmName

inline fun <reified T> T.getLogger(): KLogger = KotlinLogging.logger(T::class.jvmName)

fun String.prettifyJson() = replace(Regex("[ \\n]"), "")
    .replace(",", ", ")
    .trim()
