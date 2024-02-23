package com.doyoumate.api.auth.handler

import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.domain.auth.dto.request.LoginRequest
import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import com.doyoumate.domain.auth.dto.request.SignUpRequest
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

    fun signUp(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<SignUpRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.signUp(it))
            }

    fun login(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<LoginRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(authenticationService.login(it))
            }
}
