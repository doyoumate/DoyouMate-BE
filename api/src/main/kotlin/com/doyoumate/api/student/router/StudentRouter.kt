package com.doyoumate.api.student.router

import com.doyoumate.api.student.handler.StudentHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class StudentRouter {
    @Bean
    fun studentRoutes(handler: StudentHandler): RouterFunction<ServerResponse> =
        router {
            "/student".nest {
                GET("/applied/lectureId/{lectureId}", handler::getAppliedStudentsByLectureId)
                GET("/pre-applied/lectureId/{lectureId}", handler::getPreAppliedStudentsByLectureId)
                GET("/me", handler::getStudent)
                GET("/chapel", handler::getMyChapel)
            }
        }
}
