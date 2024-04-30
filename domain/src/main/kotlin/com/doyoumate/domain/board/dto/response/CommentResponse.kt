package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: String,
    val postId: String,
    val writer: WriterResponse,
    val content: String,
    val likedStudentIds: Set<String>,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(comment: Comment): CommentResponse =
            with(comment) {
                CommentResponse(
                    id = id!!,
                    postId = postId,
                    writer = WriterResponse(writer),
                    content = content,
                    likedStudentIds = likedStudentIds,
                    createdDate = createdDate
                )
            }
    }
}
