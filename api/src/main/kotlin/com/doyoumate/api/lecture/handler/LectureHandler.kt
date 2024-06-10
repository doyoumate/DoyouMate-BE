package com.doyoumate.api.lecture.handler

import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.api.lecture.service.LectureService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.common.util.getQueryParam
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Handler
class LectureHandler(
    private val lectureService: LectureService
) {
    fun getRelatedLecturesById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(lectureService.getRelatedLecturesById(request.pathVariable("id")))

    fun searchLecturePage(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            ServerResponse.ok()
                .body(
                    lectureService.searchLecturePage(
                        getQueryParam("year"),
                        getQueryParam("grade"),
                        getQueryParam<String>("semester")?.let { Semester(it) },
                        getQueryParam("major"),
                        getQueryParam("name")!!,
                        getQueryParam("credit"),
                        getQueryParam<String>("section")?.let { Section(it) },
                        getQueryParam("lastId"),
                        getQueryParam("size")!!
                    )
                )
        }

    fun getAppliedLectures(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(lectureService.getAppliedLectures(it.id))
            }

    fun getPreAppliedLectures(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(lectureService.getPreAppliedLectures(it.id))
            }

    fun getFilter(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(lectureService.getFilter())

    fun markLectureById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(lectureService.markLectureById(pathVariable("id"), it))
                }
        }
}
