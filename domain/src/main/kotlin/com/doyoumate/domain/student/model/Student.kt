package com.doyoumate.domain.student.model

import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Student(
    @Id
    val id: String,
    val name: String,
    val birthDate: LocalDate,
    val phoneNumber: String,
    val major: String,
    val grade: Int,
    val semester: Semester,
    val gpa: Double?,
    var lectureIds: HashSet<String> = hashSetOf()
)
