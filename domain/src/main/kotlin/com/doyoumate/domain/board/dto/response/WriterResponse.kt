package com.doyoumate.domain.board.dto.response

import com.doyoumate.domain.student.model.Student

data class WriterResponse(
    val id: String,
    val major: String,
    val grade: Int
) {
    companion object {
        operator fun invoke(student: Student): WriterResponse =
            with(student) {
                WriterResponse(
                    id = id!!,
                    major = major,
                    grade = grade
                )
            }
    }
}
