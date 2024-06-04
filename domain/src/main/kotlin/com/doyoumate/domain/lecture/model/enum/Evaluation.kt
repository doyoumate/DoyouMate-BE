package com.doyoumate.domain.lecture.model.enum

enum class Evaluation(
    val evaluationName: String
) {
    RELATIVE("상대평가"),
    ABSOLUTE("절대평가");

    companion object {
        operator fun invoke(evaluationName: String): Evaluation =
            entries.first { evaluationName.contains(it.evaluationName) }
    }
}
