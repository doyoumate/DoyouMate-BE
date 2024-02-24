package com.doyoumate.domain.review.dto.request

import com.doyoumate.domain.review.model.Review

data class CreateReviewRequest(
    val studentId: String,
    val lectureId: String,
    val score: Int,
    val content: String,
) {
    fun toEntity(): Review =
        Review(
            studentId = studentId,
            lectureId = lectureId,
            score = score,
            content = content
        )
}
