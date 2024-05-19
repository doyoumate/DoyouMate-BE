package com.doyoumate.domain.message.dto.request

data class SendMessageRequest(
    val receiverId: String,
    val content: String
)
