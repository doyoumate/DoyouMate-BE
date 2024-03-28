package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Post

data class UpdatePostRequest(
    val boardId: String,
    val title: String,
    val content: String,
) {
    fun updateEntity(post: Post): Post =
        post.copy(boardId = boardId, title = title, content = content)
}
