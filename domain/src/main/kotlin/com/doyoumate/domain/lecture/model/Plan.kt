package com.doyoumate.domain.lecture.model

import com.doyoumate.domain.lecture.model.enum.Evaluation

data class Plan(
    val ratio: Ratio,
    val overview: String,
    val objective: String,
    val type: String,
    val evaluation: Evaluation,
    val prerequisites: String
)

data class Ratio(
    val theory: Int,
    val practice: Int
)
