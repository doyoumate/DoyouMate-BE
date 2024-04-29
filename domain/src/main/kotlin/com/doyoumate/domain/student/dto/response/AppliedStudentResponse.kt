package com.doyoumate.domain.student.dto.response

import com.doyoumate.domain.student.model.Student

data class AppliedStudentResponse(
    val id: String,
    val major: String,
    val grade: Int,
    val gpa: Float?,
) {
    companion object {
        operator fun invoke(student: Student): AppliedStudentResponse =
            with(student) {
                AppliedStudentResponse(
                    id = id!!,
                    major = major,
                    grade = grade,
                    gpa = gpa,
                )
            }
    }
}
