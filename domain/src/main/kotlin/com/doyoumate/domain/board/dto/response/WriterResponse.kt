package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.board.model.Writer

data class WriterResponse(
    val id: String,
    val major: String,
    val grade: Int,
    val status: String
) {
    companion object {
        operator fun invoke(writer: Writer): WriterResponse =
            with(writer) {
                WriterResponse(
                    id = id,
                    major = major,
                    grade = grade,
                    status = status
                )
            }
    }
}
