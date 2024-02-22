package com.doyoumate.api.lecture.router

import com.doyoumate.api.lecture.handler.LectureHandler
import com.doyoumate.common.annotation.Router
import com.doyoumate.common.util.queryParams
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
                GET("/filter", handler::getFilter)
                GET("/{id}", handler::getLectureById)
                GET("", queryParams("name"), handler::searchLectures)
                GET("", handler::getLectures)
            }
        }
}
