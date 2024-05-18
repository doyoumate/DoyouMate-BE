package com.doyoumate.api.message.handler

import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.api.message.service.MessageService
import com.doyoumate.common.annotation.Handler
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Handler
class MessageHandler(
    private val messageService: MessageService
) {
    fun getMyMessages(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(messageService.getMessageBySenderIdAndReceiverId(it.id, it.id))
            }
}
