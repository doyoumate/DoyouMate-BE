package com.doyoumate.api.lecture.router

import com.doyoumate.api.lecture.handler.LectureHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class LectureRouter {
    @Bean
    fun lectureRoutes(handler: LectureHandler): RouterFunction<ServerResponse> =
        router {
            "/lecture".nest {
                GET("/{id}", handler::getLectureById)
                GET("", handler::getLectures)
            }
        }
}
