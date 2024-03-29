package com.doyoumate.common.aspect

import com.doyoumate.common.util.getLogger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Aspect
@Component
class LoggingAspect {
    private val logger = getLogger()

    @Around("@within(com.doyoumate.common.annotation.Handler)")
    fun around(joinPoint: ProceedingJoinPoint): Mono<*> =
        (joinPoint.args.first() as ServerRequest)
            .let { request ->
                request.bodyToMono<String>()
                    .defaultIfEmpty("")
                    .doOnNext {
                        logger.info {
                            "HTTP ${request.method()} ${request.uri().run { "$path${query?.let { "?$it" } ?: ""}" }} ${
                                it.replace(Regex("[ \\n]"), "")
                                    .replace(",", ", ")
                                    .trim()
                            }"
                        }
                    }
                    .map {
                        ServerRequest.from(request)
                            .body(it)
                            .build()
                    }
                    .flatMap {
                        (joinPoint.proceed(arrayOf(it)) as Mono<*>)
                            .doOnNext { logger.info { "HTTP ${(it as ServerResponse).statusCode()}" } }
                    }
            }
}
