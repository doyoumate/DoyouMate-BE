package com.doyoumate.api.review.router

import com.doyoumate.api.review.handler.ReviewHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class ReviewRouter {
    @Bean
    fun reviewRoutes(handler: ReviewHandler): RouterFunction<ServerResponse> =
        router {
            "/review".nest {
                GET("/student/{studentId}", handler::getReviewsByStudentId)
                GET("/lecture/{lectureId}", handler::getReviewsByLectureId)
                POST("", handler::createReview)
                PUT("/{id}", handler::updateReviewById)
                DELETE("/{id}", handler::deleteReviewById)
            }
        }
}
