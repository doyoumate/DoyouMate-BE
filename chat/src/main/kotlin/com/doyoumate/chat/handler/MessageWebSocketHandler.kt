package com.doyoumate.chat.handler

import com.doyoumate.common.annotation.Handler
import com.doyoumate.common.util.getLogger
import com.doyoumate.common.util.prettifyJson
import com.doyoumate.domain.message.dto.request.SendMessageRequest
import com.doyoumate.domain.message.model.Message
import com.doyoumate.domain.message.repository.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Handler
class MessageWebSocketHandler(
    private val messageRepository: MessageRepository,
    private val objectMapper: ObjectMapper,
    private val messageProducer: ReactiveKafkaProducerTemplate<String, Any>,
    private val messageConsumer: ReactiveKafkaConsumerTemplate<String, Message>,
) : WebSocketHandler {
    private val logger = getLogger()
    private val sink = Sinks.many()
        .multicast()
        .directAllOrNothing<Message>()

    override fun handle(session: WebSocketSession): Mono<Void> =
        ReactiveSecurityContextHolder.getContext()
            .map { it.authentication as DefaultJwtAuthentication }
            .doOnNext { logger.info { "WebSocket connected ${it.id}" } }
            .flatMap { authentication ->
                Mono.zip(
                    session.receive()
                        .map { it.payloadAsText }
                        .doOnNext { logger.info { "WebSocket received: ${it.prettifyJson()}" } }
                        .map { objectMapper.readValue<SendMessageRequest>(it) }
                        .flatMap {
                            messageRepository.save(
                                Message(
                                    senderId = authentication.id,
                                    receiverId = it.receiverId,
                                    content = it.content
                                )
                            )
                        }
                        .flatMap { messageProducer.send("message", authentication.id, it) }
                        .then(),
                    session.send(
                        sink.asFlux()
                            .filter { it.receiverId == authentication.id || it.senderId == authentication.id }
                            .map { objectMapper.writeValueAsString(it) }
                            .map(session::textMessage)
                            .doOnNext { logger.info { "WebSocket sent: ${it.payloadAsText.prettifyJson()}" } }
                    )
                )
            }
            .then()

    @EventListener(ApplicationStartedEvent::class)
    fun receiveMessage() {
        messageConsumer.receiveAutoAck()
            .doOnNext { sink.tryEmitNext(it.value()) }
            .subscribe()
    }
}
