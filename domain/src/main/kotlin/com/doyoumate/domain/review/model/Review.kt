package com.doyoumate.domain.review.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Review(
    @Id
    val id: String? = null,
    val studentId: String,
    val lectureId: String,
    val score: Int,
    val content: String,
    @CreatedDate
    val createdDate: LocalDate? = null
)
