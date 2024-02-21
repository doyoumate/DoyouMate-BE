package com.doyoumate.api.lecture.handler

import com.doyoumate.api.lecture.service.LectureService
import com.doyoumate.common.annotation.Handler
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Handler
class LectureHandler(
    private val lectureService: LectureService
) {
    fun getLectureById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(lectureService.getLectureById(request.pathVariable("id")))

    fun getLectures(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(lectureService.getLectures())
}
