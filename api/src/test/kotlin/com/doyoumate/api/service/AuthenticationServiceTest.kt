package com.doyoumate.api.service

import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.common.util.empty
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.auth.exception.AccountAlreadyExistException
import com.doyoumate.domain.auth.exception.CertificationAlreadyExistException
import com.doyoumate.domain.auth.exception.InvalidCertificationException
import com.doyoumate.domain.auth.exception.StudentNotFoundException
import com.doyoumate.domain.auth.repository.CertificationRepository
import com.doyoumate.domain.fixture.FROM
import com.doyoumate.domain.fixture.createCertification
import com.doyoumate.domain.fixture.createSendCertificationRequest
import com.doyoumate.domain.fixture.createStudent
import com.doyoumate.domain.student.repository.StudentRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import net.nurigo.sdk.message.service.DefaultMessageService
import reactor.kotlin.test.expectError

class AuthenticationServiceTest : BehaviorSpec() {
    private val studentRepository = mockk<StudentRepository>()

    private val certificationRepository = mockk<CertificationRepository>()

    private val messageService = mockk<DefaultMessageService>()

    private val authenticationService = AuthenticationService(
        studentRepository = studentRepository,
        certificationRepository = certificationRepository,
        messageService = messageService,
        from = FROM
    )

    init {
        Given("삼육대학교 학생인 유저가 처음 서비스를 이용하는 경우") {
            val student = createStudent(password = null)
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                }
            val certification = createCertification()
                .also {
                    every { certificationRepository.save(any()) } returns it
                }
            every { certificationRepository.findByStudentId(any()) } returns empty()
            every { messageService.sendOne(any()) } returns mockk()

            When("처음 인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("정상적으로 인증번호가 전송된다.") {
                    result.expectSubscription()
                        .verifyComplete()
                }
            }

            When("인증 요청을 시도한 후 다시 바로 인증 요청을 시도하면") {

            }
        }

        Given("삼육대학교 학생이 아닌 유저가 서비스를 이용하는 경우") {
            every { studentRepository.findById(any<String>()) } returns empty()
            every { certificationRepository.findByStudentId(any()) } returns empty()

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<StudentNotFoundException>()
                        .verify()
                }
            }
        }

        Given("이미 인증 요청을 시도한 유저가 서비스를 이용하는 경우") {
            val certification = createCertification()
                .also {
                    every { certificationRepository.findByStudentId(any()) } returns it
                }

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<CertificationAlreadyExistException>()
                        .verify()
                }
            }
        }

        Given("유효하지 않은 인증 요청을 시도한 유저가 서비스를 이용하는 경우") {
            val student = createStudent()
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                }
            every { certificationRepository.findByStudentId(any()) } returns empty()

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(
                    createSendCertificationRequest(to = "invalid_to")
                ).getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<InvalidCertificationException>()
                        .verify()
                }
            }
        }

        Given("이미 가입한 유저가 서비스를 이용하는 경우") {
            val student = createStudent()
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                }
            val certification = createCertification()
                .also {
                    every { certificationRepository.save(any()) } returns it
                }
            every { certificationRepository.findByStudentId(any()) } returns empty()

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<AccountAlreadyExistException>()
                        .verify()
                }
            }
        }
    }
}
