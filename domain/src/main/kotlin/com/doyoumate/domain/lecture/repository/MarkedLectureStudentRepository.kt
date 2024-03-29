package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.lecture.model.MarkedLectureStudent
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface MarkedLectureStudentRepository : ReactiveMongoRepository<MarkedLectureStudent, String> {
    fun findByLectureIdAndStudentId(lectureId: String, studentId: String): Mono<MarkedLectureStudent>

    fun findAllByStudentId(studentId: String): Flux<MarkedLectureStudent>
}
