package com.doyoumate.common.logging

import com.doyoumate.common.util.getLogger
import com.doyoumate.common.util.toByteArray
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
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
            .flatMap { chain.filter(it) }

    private fun ServerWebExchange.log(): Mono<ServerWebExchange> =
        request.body
            .toByteArray()
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
                            loggingResponse(this)
                            Mono.empty()
                        }
                    })
                    .build()
            }

    private fun loggingRequest(request: ServerHttpRequest, body: ByteArray) {
        request.apply {
            logger.info {
                "HTTP $method ${uri.run { "$path${query?.let { "?$it" } ?: ""}" }} ${
                    if (request.headers.contentType == MediaType.APPLICATION_JSON)
                        String(body)
                            .replace(Regex("[ \\n]"), "")
                            .replace(",", ", ")
                            .trim() else ""
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
