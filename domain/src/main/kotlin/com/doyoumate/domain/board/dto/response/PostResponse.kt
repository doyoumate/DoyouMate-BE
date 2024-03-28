package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Post
import java.time.LocalDateTime

data class PostResponse(
    val id: String,
    val boardId: String,
    val writerId: String,
    val title: String,
    val content: String,
    val likedUserIds: List<String>,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(post: Post): PostResponse =
            with(post) {
                PostResponse(
                    id = id!!,
                    boardId = boardId,
                    writerId = writerId,
                    title = title,
                    content = content,
                    likedUserIds = likedUserIds,
                    createdDate = createdDate
                )
            }
    }
}
