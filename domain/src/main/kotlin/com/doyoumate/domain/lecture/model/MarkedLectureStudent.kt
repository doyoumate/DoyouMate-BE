package com.doyoumate.domain.lecture.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MarkedLectureStudent(
    @Id
    val id: String? = null,
    val lectureId: String,
    val studentId: String
)
