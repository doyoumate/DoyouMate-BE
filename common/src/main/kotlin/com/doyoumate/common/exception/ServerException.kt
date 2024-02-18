package com.doyoumate.common.exception

abstract class ServerException(
    val code: Int,
    override val message: String
) : RuntimeException(message)
