package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Comment
import com.doyoumate.domain.student.dto.response.StudentResponse
import java.time.LocalDateTime

data class CommentResponse(
    val id: String,
    val writer: StudentResponse,
    val content: String,
    val likedUserIds: Set<String>,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(comment: Comment): CommentResponse =
            with(comment) {
                CommentResponse(
                    id = id!!,
                    writer = StudentResponse(writer),
                    content = content,
                    likedUserIds = likedUserIds,
                    createdDate = createdDate
                )
            }
    }
}
