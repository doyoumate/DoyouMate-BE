package com.doyoumate.domain.professor.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Professor(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val score: Float?,
    val role: String
)
