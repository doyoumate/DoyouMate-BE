package com.doyoumate.api.message.service

import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.domain.message.dto.response.MessageResponse
import com.doyoumate.domain.message.repository.MessageRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val studentRepository: StudentRepository
) {
    fun getMessageBySenderIdAndReceiverId(senderId: String, receiverId: String): Flux<MessageResponse> =
        Flux.zip(
            messageRepository.findAllBySenderIdOrReceiverIdOrderByCreatedDateDesc(senderId, receiverId),
            studentRepository.findById(receiverId)
                .switchIfEmpty(Mono.error(StudentNotFoundException()))
        ).map { (message, student) -> MessageResponse(message, student) }
}
