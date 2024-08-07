package com.doyoumate.api.review.service

import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.review.dto.request.CreateReviewRequest
import com.doyoumate.domain.review.dto.request.UpdateReviewRequest
import com.doyoumate.domain.review.dto.response.ReviewResponse
import com.doyoumate.domain.review.exception.ReviewNotFoundException
import com.doyoumate.domain.review.repository.ReviewRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val lectureClient: LectureClient
) {
    fun getReviewsByStudentId(studentId: String): Flux<ReviewResponse> =
        reviewRepository.findAllByStudentId(studentId)
            .map { ReviewResponse(it) }

    fun getReviewsByLectureId(lectureId: String): Flux<ReviewResponse> =
        reviewRepository.findAllByLectureId(lectureId)
            .map { ReviewResponse(it) }

    fun createReview(request: CreateReviewRequest, authentication: DefaultJwtAuthentication): Mono<ReviewResponse> =
        studentRepository.findById(authentication.id)
            .switchIfEmpty(Mono.error(StudentNotFoundException()))
            .zipWith(
                lectureRepository.findById(request.lectureId)
                    .switchIfEmpty(Mono.error(LectureNotFoundException()))
            )
            .flatMapMany { (student, lecture) ->
                lectureClient.getAppliedLectureIdsByStudentNumber(
                    student.number,
                    lecture.year,
                    lecture.semester
                )
            }
            .collectList()
            .filter { request.lectureId in it }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.save(request.toEntity()) }
            .map { ReviewResponse(it) }

    fun updateReviewById(
        id: String,
        request: UpdateReviewRequest,
        authentication: DefaultJwtAuthentication
    ): Mono<ReviewResponse> =
        reviewRepository.findById(id)
            .switchIfEmpty(Mono.error(ReviewNotFoundException()))
            .filter { it.studentId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.save(request.updateEntity(it)) }
            .map { ReviewResponse(it) }

    fun deleteReviewById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        reviewRepository.findById(id)
            .switchIfEmpty(Mono.error(ReviewNotFoundException()))
            .filter { it.studentId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { reviewRepository.deleteById(id) }
}
