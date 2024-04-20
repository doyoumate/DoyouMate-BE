package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Board
import com.doyoumate.domain.board.model.Post

data class UpdatePostRequest(
    val board: Board,
    val title: String,
    val content: String,
) {
    fun updateEntity(post: Post): Post =
        post.copy(board = board, title = title, content = content)
}
