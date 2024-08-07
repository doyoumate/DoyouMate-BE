package com.doyoumate.common.exception

import com.doyoumate.common.dto.ErrorResponse
import com.doyoumate.common.util.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    private val logger = getLogger()

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> =
        with(exchange.response) {
            val errorResponse = ErrorResponse(exception)

            logger.error { "${exception::class.simpleName}(\"${exception.message}\") at ${exception.stackTrace[0]}" }

            statusCode = HttpStatusCode.valueOf(errorResponse.code)

            writeWith(
                bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(errorResponse))
                    .toMono()
            )
        }
}
