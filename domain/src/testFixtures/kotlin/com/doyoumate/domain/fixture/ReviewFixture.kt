package com.doyoumate.domain.fixture

import com.doyoumate.domain.review.dto.request.CreateReviewRequest
import com.doyoumate.domain.review.dto.request.UpdateReviewRequest
import com.doyoumate.domain.review.dto.response.ReviewResponse
import com.doyoumate.domain.review.model.Review
import java.time.LocalDate

const val SCORE = 5
const val CONTENT = "상타치"

fun createCreateReviewRequest(
    studentId: String = ID,
    lectureId: String = ID,
    score: Int = SCORE,
    content: String = CONTENT,
): CreateReviewRequest =
    CreateReviewRequest(
        studentId = studentId,
        lectureId = lectureId,
        score = score,
        content = content
    )

fun createUpdateReviewRequest(
    score: Int = SCORE,
    content: String = CONTENT,
): UpdateReviewRequest =
    UpdateReviewRequest(
        score = score,
        content = content
    )

fun createReviewResponse(
    review: Review = createReview()
): ReviewResponse =
    ReviewResponse(review)

fun createReview(
    id: String = ID,
    studentId: String = ID,
    lectureId: String = ID,
    score: Int = SCORE,
    content: String = CONTENT,
    createdDate: LocalDate = CREATED_DATE
): Review =
    Review(
        id = id,
        studentId = studentId,
        lectureId = lectureId,
        score = score,
        content = content,
        createdDate = createdDate
    )
