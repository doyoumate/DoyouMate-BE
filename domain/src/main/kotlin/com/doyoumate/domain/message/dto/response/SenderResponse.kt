package com.doyoumate.domain.message.dto.response

import com.doyoumate.domain.student.model.Student

data class SenderResponse(
    val id: String,
    val major: String,
    val grade: Int
) {
    companion object {
        operator fun invoke(student: Student): SenderResponse =
            with(student) {
                SenderResponse(
                    id = id!!,
                    major = major,
                    grade = grade
                )
            }
    }
}
