package com.doyoumate.api.controller

import com.doyoumate.api.config.ApplicationConfiguration
import com.doyoumate.api.config.SecurityTestConfiguration
import com.doyoumate.api.student.handler.StudentHandler
import com.doyoumate.api.student.router.StudentRouter
import com.doyoumate.api.student.service.StudentService
import com.doyoumate.common.controller.ControllerTest
import com.doyoumate.common.dto.ErrorResponse
import com.doyoumate.common.util.*
import com.doyoumate.domain.fixture.ID
import com.doyoumate.domain.fixture.createStudentResponse
import com.doyoumate.domain.student.dto.response.StudentResponse
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.IsolationMode
import io.mockk.every
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@ContextConfiguration(classes = [ApplicationConfiguration::class, SecurityTestConfiguration::class])
@Import(StudentRouter::class, StudentHandler::class)
class StudentControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var studentService: StudentService

    private val studentResponseFields = listOf(
        "id" bodyDesc "식별자",
        "name" bodyDesc "이름",
        "birthDate" bodyDesc "생년월일",
        "phoneNumber" bodyDesc "전화번호",
        "major" bodyDesc "전공",
        "grade" bodyDesc "학년",
        "semester" bodyDesc "학기",
        "status" bodyDesc "상태",
        "gpa" bodyDesc "학점",
        "lectureIds" bodyDesc "수강신청한 강의 목록"
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    init {
        describe("getStudentById()는") {
            withMockUser()

            context("식별자에 해당하는 학생이 존재하는 경우") {
                every { studentService.getStudentById(any()) } returns createStudentResponse()

                it("상태 코드 200과 StudentResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/student/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<StudentResponse>()
                        .document("식별자를 통한 학생 단일 조회 성공(200)") {
                            pathParams("id" paramDesc "식별자")
                            responseBody(studentResponseFields)
                        }
                }
            }

            context("식별자에 해당하는 학생이 존재하지 않는 경우") {
                every { studentService.getStudentById(any()) } returns Mono.error(StudentNotFoundException())

                it("상태 코드 404와 ErrorResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/student/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(404)
                        .expectBody<ErrorResponse>()
                        .document("식별자를 통한 학생 단일 조회 실패(404)") {
                            pathParams("id" paramDesc "식별자")
                            responseBody(errorResponseFields)
                        }
                }
            }
        }
    }
}
