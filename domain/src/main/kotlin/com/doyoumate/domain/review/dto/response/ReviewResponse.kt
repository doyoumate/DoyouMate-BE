package com.doyoumate.domain.review.dto.response

import com.doyoumate.domain.review.model.Review
import java.time.LocalDate

data class ReviewResponse(
    val id: String,
    val studentId: String,
    val lectureId: String,
    val score: Int,
    val content: String,
    val createdDate: LocalDate
) {
    companion object {
        operator fun invoke(review: Review): ReviewResponse =
            with(review) {
                ReviewResponse(
                    id = id!!,
                    studentId = studentId,
                    lectureId = lectureId,
                    score = score,
                    content = content,
                    createdDate = createdDate!!
                )
            }
    }
}
