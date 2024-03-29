package com.doyoumate.domain.student.repository

import com.doyoumate.domain.student.model.Student
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface StudentRepository : ReactiveMongoRepository<Student, String> {
    fun findAllByAppliedLectureIdsContains(lectureIds: String): Flux<Student>

    fun findAllByPreAppliedLectureIdsContains(lectureIds: String): Flux<Student>
}
