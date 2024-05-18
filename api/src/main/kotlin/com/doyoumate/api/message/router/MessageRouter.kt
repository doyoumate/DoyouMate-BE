package com.doyoumate.api.message.router

import com.doyoumate.api.message.handler.MessageHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class MessageRouter {
    @Bean
    fun messageRoutes(handler: MessageHandler): RouterFunction<ServerResponse> =
        router {
            "/message".nest {
                GET("/my", handler::getMyMessages)
            }
        }
}
