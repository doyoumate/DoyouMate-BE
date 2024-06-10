package com.doyoumate.api.student.service

import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.student.adapter.StudentClient
import com.doyoumate.domain.student.dto.response.AppliedStudentResponse
import com.doyoumate.domain.student.dto.response.StudentResponse
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val studentClient: StudentClient
) {
    fun getStudentById(id: String): Mono<StudentResponse> =
        studentRepository.findById(id)
            .switchIfEmpty(Mono.error(StudentNotFoundException()))
            .map { StudentResponse(it) }

    fun getAppliedStudentsByLectureId(lectureId: String): Flux<AppliedStudentResponse> =
        lectureRepository.findById(lectureId)
            .flatMapMany { studentClient.getAppliedStudentIdsByLectureId(lectureId, it.year, it.semester) }
            .collectList()
            .flatMapMany { studentRepository.findAllByNumberIn(it) }
            .map { AppliedStudentResponse(it) }

    fun getPreAppliedStudentsByLectureId(lectureId: String): Flux<AppliedStudentResponse> =
        TODO("수강신청 기간에 구현")
}
