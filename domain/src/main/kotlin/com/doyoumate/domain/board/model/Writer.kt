package com.doyoumate.domain.board.model

import com.doyoumate.domain.student.model.Student

data class Writer(
    val id: String,
    val major: String,
    val grade: Int,
    val status: String
) {
    companion object {
        operator fun invoke(student: Student): Writer =
            with(student) {
                Writer(
                    id = id!!,
                    major = major,
                    grade = grade,
                    status = status
                )
            }
    }
}
