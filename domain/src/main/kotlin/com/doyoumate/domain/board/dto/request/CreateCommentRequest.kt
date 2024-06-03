package com.doyoumate.domain.board.dto.request

data class CreateCommentRequest(
    val postId: String,
    val commentId: String?,
    val content: String
)
