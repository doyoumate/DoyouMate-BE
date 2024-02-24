package com.doyoumate.api.review.service

import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.review.dto.request.CreateReviewRequest
import com.doyoumate.domain.review.dto.request.UpdateReviewRequest
import com.doyoumate.domain.review.dto.response.ReviewResponse
import com.doyoumate.domain.review.repository.ReviewRepository
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val studentRepository: StudentRepository
) {
    fun getReviewsByStudentId(studentId: String): Flux<ReviewResponse> =
        reviewRepository.findAllByStudentId(studentId)
            .map { ReviewResponse(it) }

    fun getReviewsByLectureId(lectureId: String): Flux<ReviewResponse> =
        reviewRepository.findAllByLectureId(lectureId)
            .map { ReviewResponse(it) }

    fun createReview(request: CreateReviewRequest, authentication: JwtAuthentication): Mono<ReviewResponse> =
        studentRepository.findById(authentication.id)
            .filter { request.lectureId in it.lectureIds }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.save(request.toEntity()) }
            .map { ReviewResponse(it) }

    fun updateReviewById(
        id: String,
        request: UpdateReviewRequest,
        authentication: JwtAuthentication
    ): Mono<ReviewResponse> =
        reviewRepository.findById(id)
            .filter { it.studentId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.save(request.updateEntity(it)) }
            .map { ReviewResponse(it) }

    fun deleteReviewById(id: String, authentication: JwtAuthentication): Mono<Void> =
        reviewRepository.findById(id)
            .filter { it.studentId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.deleteById(id) }
}
