package com.doyoumate.domain.message.repository

import com.doyoumate.domain.message.model.Message
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface MessageRepository : ReactiveMongoRepository<Message, String> {
    fun findAllBySenderIdOrReceiverIdOrderByCreatedDateDesc(senderId: String, receiverId: String): Flux<Message>
}
