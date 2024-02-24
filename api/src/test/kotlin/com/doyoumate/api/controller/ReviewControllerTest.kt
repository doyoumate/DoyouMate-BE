package com.doyoumate.api.controller

import com.doyoumate.api.config.ApplicationConfiguration
import com.doyoumate.api.config.SecurityTestConfiguration
import com.doyoumate.api.review.handler.ReviewHandler
import com.doyoumate.api.review.router.ReviewRouter
import com.doyoumate.api.review.service.ReviewService
import com.doyoumate.common.controller.ControllerTest
import com.doyoumate.common.dto.ErrorResponse
import com.doyoumate.common.util.*
import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.fixture.ID
import com.doyoumate.domain.fixture.createCreateReviewRequest
import com.doyoumate.domain.fixture.createReviewResponse
import com.doyoumate.domain.fixture.createUpdateReviewRequest
import com.doyoumate.domain.review.dto.response.ReviewResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.IsolationMode
import io.mockk.every
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@ContextConfiguration(classes = [ApplicationConfiguration::class, SecurityTestConfiguration::class])
@Import(ReviewRouter::class, ReviewHandler::class)
class ReviewControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var reviewService: ReviewService

    private val createReviewRequestFields = listOf(
        "studentId" bodyDesc "학생 식별자",
        "lectureId" bodyDesc "강의 식별자",
        "score" bodyDesc "점수",
        "content" bodyDesc "내용"
    )

    private val updateReviewRequestFields = listOf(
        "score" bodyDesc "점수",
        "content" bodyDesc "내용"
    )

    private val reviewResponseFields = listOf(
        "id" bodyDesc "식별자",
        "studentId" bodyDesc "학생 식별자",
        "lectureId" bodyDesc "강의 식별자",
        "score" bodyDesc "점수",
        "content" bodyDesc "내용",
        "createdDate" bodyDesc "작성 날짜"
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    init {
        describe("getReviewsByStudentId()는") {
            withMockUser()

            context("학생이 작성한 강의 평가가 있는 경우") {
                every { reviewService.getReviewsByStudentId(any()) } returns listOf(createReviewResponse())

                it("상태 코드 200과 ReviewResponse들을 반환한다.") {
                    webClient.get()
                        .uri("/review/student/{studentId}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<ReviewResponse>>()
                        .document("학생 식별자를 통한 강의 평가 전체 조회 성공(200)") {
                            pathParams("studentId" paramDesc "학생 식별자")
                            responseBody(reviewResponseFields.toListFields())
                        }

                }
            }
        }

        describe("getReviewsByLectureId()는") {
            withMockUser()

            context("강의에 작성된 강의 평가가 있는 경우") {
                every { reviewService.getReviewsByLectureId(any()) } returns listOf(createReviewResponse())

                it("상태 코드 200과 ReviewResponse들을 반환한다.") {
                    webClient.get()
                        .uri("/review/lecture/{lectureId}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<ReviewResponse>>()
                        .document("강의 식별자를 통한 강의 평가 전체 조회 성공(200)") {
                            pathParams("lectureId" paramDesc "강의 식별자")
                            responseBody(reviewResponseFields.toListFields())
                        }

                }
            }
        }

        describe("createReview()는") {
            withMockUser()

            context("강의를 수강한 학생의 평가가 주어지는 경우") {
                every { reviewService.createReview(any(), any()) } returns createReviewResponse()

                it("상태 코드 200과 ReviewResponse를 반환한다.") {
                    webClient.post()
                        .uri("/review")
                        .bodyValue(createCreateReviewRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<ReviewResponse>()
                        .document("강의 평가 생성 성공(200)") {
                            requestBody(createReviewRequestFields)
                            responseBody(reviewResponseFields)
                        }
                }
            }

            context("강의를 수강하지 않은 학생의 평가가 주어지는 경우") {
                every { reviewService.createReview(any(), any()) } returns Mono.error(PermissionDeniedException())

                it("상태 코드 403과 ErrorResponse를 반환한다.") {
                    webClient.post()
                        .uri("/review")
                        .bodyValue(createCreateReviewRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(403)
                        .expectBody<ErrorResponse>()
                        .document("강의 평가 생성 실패(403)") {
                            requestBody(createReviewRequestFields)
                            responseBody(errorResponseFields)
                        }

                }
            }
        }

        describe("updateReviewById()는") {
            withMockUser()

            context("강의를 수강한 학생의 평가가 주어지는 경우") {
                every { reviewService.updateReviewById(any(), any(), any()) } returns createReviewResponse()

                it("상태 코드 200과 ReviewResponse를 반환한다.") {
                    webClient.put()
                        .uri("/review/{id}", ID)
                        .bodyValue(createUpdateReviewRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<ReviewResponse>()
                        .document("강의 평가 수정 성공(200)") {
                            pathParams("id" paramDesc "식별자")
                            requestBody(updateReviewRequestFields)
                            responseBody(reviewResponseFields)
                        }
                }
            }

            context("강의를 수강하지 않은 학생의 평가가 주어지는 경우") {
                every { reviewService.updateReviewById(any(), any(), any()) } returns
                    Mono.error(PermissionDeniedException())

                it("상태 코드 403과 ErrorResponse를 반환한다.") {
                    webClient.put()
                        .uri("/review/{id}", ID)
                        .bodyValue(createUpdateReviewRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(403)
                        .expectBody<ErrorResponse>()
                        .document("강의 평가 수정 실패(403)") {
                            pathParams("id" paramDesc "식별자")
                            requestBody(updateReviewRequestFields)
                            responseBody(errorResponseFields)
                        }

                }
            }
        }

        describe("deleteReviewById()는") {
            withMockUser()

            context("본인의 평가에 대한 식별자가 주어지는 경우") {
                every { reviewService.deleteReviewById(any(), any()) } returns Mono.empty()

                it("상태 코드 200을 반환한다.") {
                    webClient.delete()
                        .uri("/review/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<Void>()
                        .document("강의 평가 삭제 성공(200)") {
                            pathParams("id" paramDesc "식별자")
                        }
                }
            }

            context("다른 학생의 평가에 대한 식별자가 주어지는 경우") {
                every { reviewService.deleteReviewById(any(), any()) } returns
                    Mono.error(PermissionDeniedException())

                it("상태 코드 403과 ErrorResponse를 반환한다.") {
                    webClient.delete()
                        .uri("/review/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(403)
                        .expectBody<ErrorResponse>()
                        .document("강의 평가 삭제 실패(403)") {
                            pathParams("id" paramDesc "식별자")
                            responseBody(errorResponseFields)
                        }

                }
            }
        }
    }
}
