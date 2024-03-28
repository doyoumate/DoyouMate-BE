package com.doyoumate.domain.board.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Post(
    @Id
    val id: String? = null,
    val boardId: String,
    val writerId: String,
    val title: String,
    val content: String,
    val likedUserIds: List<String>,
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)
