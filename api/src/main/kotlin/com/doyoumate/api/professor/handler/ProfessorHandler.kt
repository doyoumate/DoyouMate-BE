package com.doyoumate.api.professor.handler

import com.doyoumate.api.professor.service.ProfessorService
import com.doyoumate.common.annotation.Handler
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Handler
class ProfessorHandler(
    private val professorService: ProfessorService
) {
    fun getProfessorById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(professorService.getProfessorById(request.pathVariable("id")))
}
