package com.doyoumate.common.logging

import com.doyoumate.common.util.getLogger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class LoggingFilter : WebFilter {
    private val logger = getLogger()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        exchange.log()
            .flatMap {
                chain.filter(it)
            }

    private fun ServerWebExchange.log(): Mono<ServerWebExchange> =
        request.bodyToByteArray()
            .doOnNext { loggingRequest(request, it) }
            .map {
                mutate()
                    .request(object : ServerHttpRequestDecorator(request) {
                        override fun getBody(): Flux<DataBuffer> =
                            Flux.just(
                                response.bufferFactory()
                                    .wrap(it)
                            )
                    })
                    .response(response.apply {
                        beforeCommit {
                            loggingResponse(response)
                            Mono.empty()
                        }
                    })
                    .build()
            }

    private fun ServerHttpRequest.bodyToByteArray(): Mono<ByteArray> =
        DataBufferUtils
            .join(this.body)
            .map { buffer ->
                ByteArray(buffer.readableByteCount())
                    .also { DataBufferUtils.release(buffer.read(it)) }
            }
            .defaultIfEmpty(ByteArray(0))

    private fun loggingRequest(request: ServerHttpRequest, body: ByteArray) {
        request.apply {
            logger.info {
                "HTTP $method ${uri.run { "$path${query?.let { "?$it" } ?: ""}" }} ${
                    String(body)
                        .replace(Regex("[ \\n]"), "")
                        .replace(",", ", ")
                        .trim()
                }"
            }
        }
    }

    private fun loggingResponse(response: ServerHttpResponse) {
        response.apply {
            logger.info { "HTTP $statusCode" }
        }
    }
}
