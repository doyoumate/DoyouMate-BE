package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Board

data class UpdateBoardRequest(
    val name: String
) {
    fun updateEntity(board: Board): Board =
        board.copy(name = name)
}
