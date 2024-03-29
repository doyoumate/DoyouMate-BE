package com.doyoumate.domain.board.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Comment(
    @Id
    val id: String? = null,
    val postId: String,
    val writerId: String,
    val content: String,
    val likedUserIds: Set<String>,
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)
