package com.doyoumate.api.student.service

import com.doyoumate.domain.student.dto.response.StudentResponse
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class StudentService(
    private val studentRepository: StudentRepository
) {
    fun getStudentById(id: String): Mono<StudentResponse> =
        studentRepository.findById(id)
            .switchIfEmpty { Mono.error(StudentNotFoundException()) }
            .map { StudentResponse(it) }
}