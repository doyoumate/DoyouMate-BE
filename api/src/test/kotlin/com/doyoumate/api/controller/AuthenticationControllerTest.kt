package com.doyoumate.api.controller

import com.doyoumate.api.auth.handler.AuthenticationHandler
import com.doyoumate.api.auth.router.AuthenticationRouter
import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.api.config.ApplicationConfiguration
import com.doyoumate.api.config.SecurityTestConfiguration
import com.doyoumate.common.controller.ControllerTest
import com.doyoumate.common.util.bodyDesc
import com.doyoumate.common.util.document
import com.doyoumate.common.util.empty
import com.doyoumate.domain.fixture.createSendCertificationRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.expectBody

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
                every { authenticationService.sendCertification(any()) } returns empty()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .post()
                        .uri("/auth/certificate")
                        .bodyValue(createSendCertificationRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<Void>()
                        .document("회원가입을 위한 인증번호 요청 성공(200)") {
                            requestBody(sendCertificationRequestFields)
                        }
                }
            }
        }
    }
}
