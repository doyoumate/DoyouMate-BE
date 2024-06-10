package com.doyoumate.domain.student.repository

import com.doyoumate.domain.student.model.Student
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface StudentRepository : ReactiveMongoRepository<Student, String> {
    fun findByNumber(number: String): Mono<Student>

    fun findAllByNumberIn(numbers: List<String>): Flux<Student>
}
