package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Board

data class BoardResponse(
    val id: String,
    val name: String
) {
    companion object {
        operator fun invoke(board: Board): BoardResponse =
            with(board) {
                BoardResponse(
                    id = id!!,
                    name = name
                )
            }
    }
}
