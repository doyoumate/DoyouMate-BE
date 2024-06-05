package com.doyoumate.api.professor.router

import com.doyoumate.api.professor.handler.ProfessorHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class ProfessorRouter {
    @Bean
    fun professorRoutes(handler: ProfessorHandler): RouterFunction<ServerResponse> =
        router {
            "/professor".nest {
                GET("/{id}", handler::getProfessorById)
            }
        }
}
