package com.doyoumate.api.service

import com.doyoumate.api.review.service.ReviewService
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.fixture.*
import com.doyoumate.domain.review.repository.ReviewRepository
import com.doyoumate.domain.student.repository.StudentRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class ReviewServiceTest : BehaviorSpec() {
    private val reviewRepository = mockk<ReviewRepository>()

    private val studentRepository = mockk<StudentRepository>()

    private val reviewService = ReviewService(
        reviewRepository = reviewRepository,
        studentRepository = studentRepository
    )

    init {
        Given("강의와 학생이 존재하는 경우") {
            val review = createReview()
                .also {
                    every { reviewRepository.findById(any<String>()) } returns it
                    every { reviewRepository.findAllByStudentId(any()) } returns listOf(it)
                    every { reviewRepository.findAllByLectureId(any()) } returns listOf(it)
                    every { reviewRepository.save(any()) } returns it
                    every { reviewRepository.deleteById(any<String>()) } returns Mono.empty()
                }
            val student = createStudent()
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                }

            When("특정 학생이 작성한 평가들을 조회하는 경우") {
                val result = reviewService.getReviewsByStudentId(ID)
                    .getResult()

                Then("해당 학생이 작성한 평가들이 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createReviewResponse(review))
                        .verifyComplete()
                }
            }

            When("특정 강의에 작성된 평가들을 조회하는 경우") {
                val result = reviewService.getReviewsByLectureId(ID)
                    .getResult()

                Then("해당 강의에 작성된 평가들이 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createReviewResponse(review))
                        .verifyComplete()
                }
            }

            When("내가 수강한 강의에 평가를 작성하는 경우") {
                val result = reviewService.createReview(createCreateReviewRequest(), createJwtAuthentication())
                    .getResult()

                Then("해당 강의에 평가가 작성된다.") {
                    result.expectSubscription()
                        .expectNext(createReviewResponse(review))
                        .verifyComplete()
                }
            }

            When("내가 수강하지 않은 강의에 평가를 작성하는 경우") {
                val result = reviewService.createReview(
                    createCreateReviewRequest(lectureId = "invalid_id"), createJwtAuthentication()
                ).getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("내가 작성한 평가를 수정하는 경우") {
                val result = reviewService.updateReviewById(ID, createUpdateReviewRequest(), createJwtAuthentication())
                    .getResult()

                Then("해당 평가가 수정된다.") {
                    result.expectSubscription()
                        .expectNext(createReviewResponse(review))
                        .verifyComplete()
                }
            }

            When("내가 작성한 평가를 삭제하는 경우") {
                val result = reviewService.deleteReviewById(ID, createJwtAuthentication())
                    .getResult()

                Then("해당 평가가 삭제된다.") {
                    result.expectSubscription()
                        .verifyComplete()
                }
            }

            When("다른 학생의 평가를 수정하는 경우") {
                val result = reviewService.updateReviewById(
                    ID, createUpdateReviewRequest(), createJwtAuthentication(id = "invalid_id")
                ).getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("다른 학생의 평가를 삭제하는 경우") {
                val result = reviewService.deleteReviewById(ID, createJwtAuthentication(id = "invalid_id"))
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }
        }
    }
}
