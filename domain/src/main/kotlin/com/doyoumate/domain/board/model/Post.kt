package com.doyoumate.domain.board.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Post(
    @Id
    val id: String? = null,
    val board: Board,
    val writer: Writer,
    val title: String,
    val content: String,
    val likedStudentIds: HashSet<String> = hashSetOf(),
    val commentIds: HashSet<String> = hashSetOf(),
    @Indexed
    @CreatedDate
    val createdDate: LocalDateTime? = null,
    val deletedDate: LocalDateTime? = null
)
