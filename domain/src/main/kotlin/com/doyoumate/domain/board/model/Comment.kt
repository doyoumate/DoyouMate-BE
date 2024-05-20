package com.doyoumate.domain.board.model

import com.doyoumate.domain.student.model.Student
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Comment(
    @Id
    val id: String? = null,
    val postId: String,
    val writer: Writer,
    val content: String,
    val likedStudentIds: HashSet<String> = hashSetOf(),
    @Indexed
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)
