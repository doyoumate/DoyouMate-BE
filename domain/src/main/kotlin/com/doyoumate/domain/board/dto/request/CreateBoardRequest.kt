package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Board

data class CreateBoardRequest(
    val name: String
) {
    fun toEntity(): Board = Board(name = name)
}
