package com.doyoumate.domain.lecture.dto.response

import com.doyoumate.domain.lecture.model.Plan
import com.doyoumate.domain.lecture.model.Ratio

data class PlanResponse(
    val ratio: Ratio,
    val overview: String,
    val objective: String,
    val type: String,
    val evaluation: String,
    val prerequisites: String
) {
    companion object {
        operator fun invoke(plan: Plan): PlanResponse =
            with(plan) {
                PlanResponse(
                    ratio = ratio,
                    overview = overview,
                    objective = objective,
                    type = type,
                    evaluation = evaluation.evaluationName,
                    prerequisites = prerequisites
                )
            }
    }
}
