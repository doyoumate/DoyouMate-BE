package com.doyoumate.api.review.handler

import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.api.review.service.ReviewService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.domain.review.dto.request.CreateReviewRequest
import com.doyoumate.domain.review.dto.request.UpdateReviewRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Handler
class ReviewHandler(
    private val reviewService: ReviewService
) {
    fun getReviewsByStudentId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(reviewService.getReviewsByStudentId(request.pathVariable("studentId")))

    fun getReviewsByLectureId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(reviewService.getReviewsByLectureId(request.pathVariable("lectureId")))

    fun createReview(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(bodyToMono<CreateReviewRequest>(), getAuthentication())
                .flatMap { (createReviewRequest, authentication) ->
                    ServerResponse.ok()
                        .body(reviewService.createReview(createReviewRequest, authentication))
                }
        }

    fun updateReviewById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(bodyToMono<UpdateReviewRequest>(), getAuthentication())
                .flatMap { (updateReviewRequest, authentication) ->
                    ServerResponse.ok()
                        .body(reviewService.updateReviewById(pathVariable("id"), updateReviewRequest, authentication))
                }
        }

    fun deleteReviewById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(reviewService.deleteReviewById(pathVariable("id"), it))
                }
        }
}
