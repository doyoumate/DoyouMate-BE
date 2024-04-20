package com.doyoumate.domain.board.model

import com.doyoumate.domain.student.model.Student
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Comment(
    @Id
    val id: String? = null,
    val writer: Student,
    val content: String,
    val likedUserIds: HashSet<String> = hashSetOf(),
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)
