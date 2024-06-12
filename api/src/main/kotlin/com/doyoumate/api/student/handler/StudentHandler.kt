package com.doyoumate.api.student.handler

import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.api.student.service.StudentService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.time.LocalDate

@Handler
class StudentHandler(
    private val studentService: StudentService
) {
    fun getStudent(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(studentService.getStudentById(it.id))
            }

    fun getAppliedStudentsByLectureId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(studentService.getAppliedStudentsByLectureId(request.pathVariable("lectureId")))

    fun getPreAppliedStudentsByLectureId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(studentService.getPreAppliedStudentsByLectureId(request.pathVariable("lectureId")))

    fun getMyChapel(request: ServerRequest): Mono<ServerResponse> =
        with(request to LocalDate.now()) {
            first.getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(studentService.getChapelByIdAndYearAndSemester(it.id, second.year, Semester(second)))
                }
        }
}
