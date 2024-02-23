package com.doyoumate.api.service

import com.doyoumate.api.student.service.StudentService
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.fixture.ID
import com.doyoumate.domain.fixture.createStudent
import com.doyoumate.domain.fixture.createStudentResponse
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class StudentServiceTest : BehaviorSpec() {
    private val studentRepository = mockk<StudentRepository>()

    private val studentService = StudentService(
        studentRepository = studentRepository
    )

    init {
        Given("강의가 존재하는 경우") {
            val student = createStudent()
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                }

            When("식별자를 통해 특정 학생을 조회하면") {
                val result = studentService.getStudentById(ID)
                    .getResult()

                Then("해당 학생이 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createStudentResponse())
                        .verifyComplete()
                }
            }
        }

        Given("학생이 존재하지 않는 경우") {
            every { studentRepository.findById(any<String>()) } returns Mono.empty()

            When("식별자를 통해 특정 학생을 조회하면") {
                val result = studentService.getStudentById(ID)
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<StudentNotFoundException>()
                        .verify()
                }
            }
        }
    }
}
