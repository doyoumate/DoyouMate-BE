package com.doyoumate.api.service

import com.doyoumate.api.auth.service.AuthenticationService
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.auth.exception.*
import com.doyoumate.domain.auth.repository.CertificationRepository
import com.doyoumate.domain.fixture.*
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import net.nurigo.sdk.message.service.DefaultMessageService
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class AuthenticationServiceTest : BehaviorSpec() {
    private val studentRepository = mockk<StudentRepository>()

    private val certificationRepository = mockk<CertificationRepository>()

    private val messageService = mockk<DefaultMessageService>()

    private val authenticationService = AuthenticationService(
        studentRepository = studentRepository,
        certificationRepository = certificationRepository,
        passwordEncoder = passwordEncoder,
        jwtProvider = jwtProvider,
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
            every { certificationRepository.findByStudentId(any()) } returns Mono.empty()
            every { messageService.sendOne(any()) } returns mockk()

            When("처음 인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("정상적으로 인증번호가 전송된다.") {
                    result.expectSubscription()
                        .verifyComplete()
                }
            }

            When("인증 없이 회원가입을 시도하면") {
                val result = authenticationService.signUp(createSignUpRequest())
                    .getResult()

                Then("회원가입이 안된다.") {
                    result.expectSubscription()
                        .expectError<InvalidCertificationException>()
                        .verify()
                }
            }

            When("로그인을 시도하면") {
                val result = authenticationService.login(createLoginRequest())
                    .getResult()

                Then("로그인이 거부된다.") {
                    result.expectSubscription()
                        .expectError<AccountNotFoundException>()
                        .verify()
                }
            }
        }

        Given("삼육대학교 학생이 아닌 유저가 서비스를 이용하는 경우") {
            every { studentRepository.findById(any<String>()) } returns Mono.empty()
            every { certificationRepository.findByStudentId(any()) } returns Mono.empty()

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<StudentNotFoundException>()
                        .verify()
                }
            }

            When("로그인을 시도하면") {
                val result = authenticationService.login(createLoginRequest())
                    .getResult()

                Then("로그인이 거부된다.") {
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
            every { certificationRepository.findByStudentId(any()) } returns Mono.empty()

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
            every { certificationRepository.findByStudentId(any()) } returns Mono.empty()

            When("인증 요청을 시도하면") {
                val result = authenticationService.sendCertification(createSendCertificationRequest())
                    .getResult()

                Then("인증 요청이 거부된다.") {
                    result.expectSubscription()
                        .expectError<AccountAlreadyExistException>()
                        .verify()
                }
            }

            When("올바른 정보로 로그인을 시도하면") {
                val result = authenticationService.login(createLoginRequest())
                    .getResult()

                Then("로그인이 성공한다.") {
                    result.expectSubscription()
                        .expectNext(createLoginResponse())
                        .verifyComplete()
                }
            }

            When("올바르지 않은 정보로 로그인을 시도하면") {
                val result = authenticationService.login(createLoginRequest(password = "invalid_password"))
                    .getResult()

                Then("로그인이 실패한다.") {
                    result.expectSubscription()
                        .expectError<PasswordNotMatchedException>()
                        .verify()
                }
            }
        }

        Given("인증을 완료한 유저의 경우") {
            val student = createStudent(password = null)
                .also {
                    every { studentRepository.findById(any<String>()) } returns it
                    every { studentRepository.save(any()) } returns it.copy(password = PASSWORD)
                }
            val certification = createCertification()
                .also {
                    every { certificationRepository.findByStudentId(any()) } returns it
                }

            When("회원가입을 시도하면") {
                val result = authenticationService.signUp(createSignUpRequest())
                    .getResult()

                Then("정상적으로 회원가입이 완료된다.") {
                    result.expectSubscription()
                        .verifyComplete()
                }
            }
        }
    }
}
