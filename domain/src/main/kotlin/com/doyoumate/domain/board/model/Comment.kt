package com.doyoumate.domain.board.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Comment(
    @Id
    val id: String,
    val writerId: String,
    val content: String,
    @CreatedDate
    val createdDate: LocalDateTime
)
