package com.doyoumate.domain.board.dto.request

data class CreatePostRequest(
    val boardId: String,
    val title: String,
    val content: String,
)
