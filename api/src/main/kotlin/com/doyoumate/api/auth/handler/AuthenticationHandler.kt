package com.doyoumate.api.auth.handler

import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class AuthenticationHandler(
    private val authenticationService: AuthenticationService
) {
    fun sendCertification(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<SendCertificationRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.sendCertification(it))
            }
}
