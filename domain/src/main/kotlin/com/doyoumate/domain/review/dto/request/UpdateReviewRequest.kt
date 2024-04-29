package com.doyoumate.domain.review.dto.request

import com.doyoumate.domain.review.model.Review

data class UpdateReviewRequest(
    val score: Int,
    val content: String,
) {
    fun updateEntity(review: Review): Review =
        review.copy(
            score = score,
            content = content
        )
}
