package com.doyoumate.api.auth.router

import com.doyoumate.api.auth.handler.AuthenticationHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class AuthenticationRouter {
    @Bean
    fun authenticationRoutes(handler: AuthenticationHandler): RouterFunction<ServerResponse> =
        router {
            "/auth".nest {
                POST("/certificate", handler::sendCertification)
            }
        }
}
