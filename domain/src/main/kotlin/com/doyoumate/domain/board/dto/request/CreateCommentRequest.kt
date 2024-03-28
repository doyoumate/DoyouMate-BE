package com.doyoumate.domain.board.dto.request

data class CreateCommentRequest(
    val postId: String,
    val content: String
)
