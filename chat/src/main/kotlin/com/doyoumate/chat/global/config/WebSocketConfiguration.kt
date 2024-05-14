package com.doyoumate.chat.global.config

import com.doyoumate.chat.handler.MessageWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping

@Configuration
class WebSocketConfiguration {
    @Bean
    fun handlerMapping(messageWebSocketHandler: MessageWebSocketHandler): HandlerMapping =
        SimpleUrlHandlerMapping(mapOf("" to messageWebSocketHandler), -1)
}
