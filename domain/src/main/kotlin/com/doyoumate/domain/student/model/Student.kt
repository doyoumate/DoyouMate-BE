package com.doyoumate.domain.student.model

import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.student.model.enum.Role
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Student(
    @Id
    val id: String? = null,
    val number: String,
    val name: String,
    val password: String? = null,
    val birthDate: LocalDate,
    val phoneNumber: String,
    val major: String,
    val grade: Int,
    val semester: Semester,
    val status: String,
    val gpa: Float?,
    val rank: Int?,
    val role: Role = Role.USER,
    val markedLecturesIds: HashSet<String> = hashSetOf()
)
