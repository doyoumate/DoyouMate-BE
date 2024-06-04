package com.doyoumate.common.logging

import com.doyoumate.common.util.getLogger
import com.doyoumate.common.util.toByteArray
import com.doyoumate.common.util.toPrettyJson
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
        mutate()
            .let { builder ->
                Mono.just(request)
                    .filter { it.headers.contentType == MediaType.APPLICATION_JSON }
                    .flatMap {
                        request.body
                            .toByteArray()
                    }
                    .doOnNext {
                        builder.request(
                            object : ServerHttpRequestDecorator(request) {
                                override fun getBody(): Flux<DataBuffer> =
                                    Flux.just(
                                        response.bufferFactory()
                                            .wrap(it)
                                    )
                            }
                        )
                    }
                    .defaultIfEmpty(ByteArray(0))
                    .doOnNext { loggingRequest(request, String(it)) }
                    .map {
                        builder
                            .response(response.apply {
                                beforeCommit {
                                    loggingResponse(this)
                                    Mono.empty()
                                }
                            })
                            .build()
                    }
            }

    private fun loggingRequest(request: ServerHttpRequest, body: String) {
        request.apply {
            logger.info {
                "HTTP $method ${uri.run { "$path${query?.let { "?$it" } ?: ""}" }} ${body.toPrettyJson()}"
            }
        }
    }

    private fun loggingResponse(response: ServerHttpResponse) {
        logger.info { "HTTP ${response.statusCode}" }
    }
}
