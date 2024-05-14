package com.doyoumate.domain.message.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Message(
    @Id
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val content: String,
    @Indexed
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)
