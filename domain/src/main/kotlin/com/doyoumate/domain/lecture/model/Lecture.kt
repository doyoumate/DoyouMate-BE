package com.doyoumate.domain.lecture.model

import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Lecture(
    @Id
    val id: String,
    val year: Int,
    val grade: Int,
    val semester: Semester,
    val major: String,
    val name: String,
    val professor: String,
    val room: String,
    val date: String,
    val credit: Int,
    val section: Section?,
)
