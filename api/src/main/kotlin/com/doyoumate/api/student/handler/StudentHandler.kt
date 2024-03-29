package com.doyoumate.api.student.handler

import com.doyoumate.api.student.service.StudentService
import com.doyoumate.common.annotation.Handler
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Handler
class StudentHandler(
    private val studentService: StudentService
) {
    fun getStudentById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(studentService.getStudentById(request.pathVariable("id")))

    fun getAppliedStudentsByLectureId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(studentService.getAppliedStudentsByLectureId(request.pathVariable("lectureId")))

    fun getPreAppliedStudentsByLectureId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(studentService.getPreAppliedStudentsByLectureId(request.pathVariable("lectureId")))
}
