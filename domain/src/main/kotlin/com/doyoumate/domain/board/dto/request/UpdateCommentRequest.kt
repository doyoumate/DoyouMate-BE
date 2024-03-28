package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Comment

data class UpdateCommentRequest(
    val content: String
) {
    fun updateEntity(comment: Comment): Comment =
        comment.copy(content = content)
}
