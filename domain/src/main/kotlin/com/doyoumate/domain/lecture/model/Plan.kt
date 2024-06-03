package com.doyoumate.domain.lecture.model

import com.doyoumate.common.util.getValue
import com.fasterxml.jackson.databind.JsonNode

data class Plan(
    val lectureRatio: LectureRatio,
    val overview: String,
    val objective: String,
    val type: String,
    val evaluation: String,
    val prerequisites: String
) {
    companion object {
        operator fun invoke(node: JsonNode): Plan =
            with(node) {
                Plan(
                    lectureRatio = LectureRatio(
                        theory = getValue<String>("THEORY_WKHS").ifBlank { "0" }.toInt(),
                        practice = getValue<String>("PRAC_WKHS").ifBlank { "0" }.toInt()
                    ),
                    overview = getValue("LT_PURP_SMRY_CTNT"),
                    objective = getValue("LRN_TGET_CTNT"),
                    type = getValue("SBJT_CLSF_CD"),
                    evaluation = getValue("LSRT_EVAL_DIV_CD"),
                    prerequisites = getValue("PRE_LRN_CTNT")
                )
            }
    }
}

data class LectureRatio(
    val theory: Int,
    val practice: Int
)
