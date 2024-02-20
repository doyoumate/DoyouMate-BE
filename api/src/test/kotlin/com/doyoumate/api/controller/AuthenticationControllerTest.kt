package com.doyoumate.api.controller

import com.doyoumate.api.auth.handler.AuthenticationHandler
import com.doyoumate.api.auth.router.AuthenticationRouter
import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.api.config.ApplicationConfiguration
import com.doyoumate.api.config.SecurityTestConfiguration
import com.doyoumate.common.controller.ControllerTest
import com.doyoumate.common.util.bodyDesc
import com.doyoumate.common.util.document
import com.doyoumate.domain.auth.exception.AccountAlreadyExistException
import com.doyoumate.domain.auth.exception.CertificationAlreadyExistException
import com.doyoumate.domain.auth.exception.InvalidCertificationException
import com.doyoumate.domain.auth.exception.StudentNotFoundException
import com.doyoumate.domain.fixture.createSendCertificationRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@ContextConfiguration(classes = [ApplicationConfiguration::class, SecurityTestConfiguration::class])
@Import(AuthenticationRouter::class, AuthenticationHandler::class)
class AuthenticationControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    private val sendCertificationRequestFields = listOf(
        "studentId" bodyDesc "학번",
        "to" bodyDesc "전화번호"
    )

    init {
        describe("sendCertification()은") {
            context("처음 가입하는 학생이 인증을 요청하는 경우") {
                every { authenticationService.sendCertification(any()) } returns Mono.empty()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증 요청 성공(200)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }


            context("유효한 인증 요청이 아닌 경우") {
                every { authenticationService.sendCertification(any()) } returns
                    Mono.error(InvalidCertificationException())

                it("상태 코드 403을 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(403)
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증 요청 실패(403)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }

            context("삼육대학교 학생이 아닌 경우") {
                every { authenticationService.sendCertification(any()) } returns
                    Mono.error(StudentNotFoundException())

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(404)
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증 요청 실패(404)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }

            context("이미 가입한 학생이 인증을 요청하는 경우") {
                every { authenticationService.sendCertification(any()) } returns
                    Mono.error(AccountAlreadyExistException())

                it("상태 코드 409를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(409)
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증 요청 실패(409 - 1)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }


            context("이미 인증 요청을 보낸 경우") {
                every { authenticationService.sendCertification(any()) } returns
                    Mono.error(CertificationAlreadyExistException())

                it("상태 코드 409를 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(409)
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증 요청 실패(409 - 12)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }
        }
    }
}
