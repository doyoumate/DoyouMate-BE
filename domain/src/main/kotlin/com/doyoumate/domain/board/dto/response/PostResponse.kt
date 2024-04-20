package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.student.dto.response.StudentResponse
import java.time.LocalDateTime

data class PostResponse(
    val id: String,
    val board: BoardResponse,
    val writer: StudentResponse,
    val title: String,
    val content: String,
    val likedUserIds: Set<String>,
    val commentIds: Set<String>,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(post: Post): PostResponse =
            with(post) {
                PostResponse(
                    id = id!!,
                    board = BoardResponse(board),
                    writer = StudentResponse(writer),
                    title = title,
                    content = content,
                    likedUserIds = likedUserIds,
                    commentIds = commentIds,
                    createdDate = createdDate
                )
            }
    }
}
