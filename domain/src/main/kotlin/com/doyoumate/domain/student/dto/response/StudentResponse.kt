package com.doyoumate.domain.student.dto.response

import com.doyoumate.domain.student.model.Student
import java.time.LocalDate

data class StudentResponse(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
    val phoneNumber: String?,
    val major: String,
    val grade: Int,
    val semester: String,
    val status: String,
    val gpa: Float?,
    val appliedLectureIds: HashSet<String>,
    val preAppliedLectureIds: HashSet<String>
) {
    companion object {
        operator fun invoke(student: Student): StudentResponse =
            with(student) {
                StudentResponse(
                    id = id,
                    name = name,
                    birthDate = birthDate,
                    phoneNumber = phoneNumber,
                    major = major,
                    grade = grade,
                    semester = semester.semesterName,
                    status = status,
                    gpa = gpa,
                    appliedLectureIds = appliedLectureIds,
                    preAppliedLectureIds = preAppliedLectureIds
                )
            }
    }
}
