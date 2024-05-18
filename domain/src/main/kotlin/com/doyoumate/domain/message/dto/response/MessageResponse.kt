package com.doyoumate.domain.message.dto.response

import com.doyoumate.domain.message.model.Message
import com.doyoumate.domain.student.model.Student
import java.time.LocalDateTime

data class MessageResponse(
    val id: String,
    val sender: SenderResponse,
    val receiverId: String,
    val content: String,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(message: Message, sender: Student): MessageResponse =
            with(message) {
                MessageResponse(
                    id = id!!,
                    sender = SenderResponse(sender),
                    receiverId = receiverId,
                    content = content,
                    createdDate = createdDate
                )
            }
    }
}
