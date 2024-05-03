package com.doyoumate.api.auth.service

import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.domain.auth.dto.request.LoginRequest
import com.doyoumate.domain.auth.dto.request.RefreshRequest
import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import com.doyoumate.domain.auth.dto.request.SignUpRequest
import com.doyoumate.domain.auth.dto.response.LoginResponse
import com.doyoumate.domain.auth.dto.response.RefreshResponse
import com.doyoumate.domain.auth.exception.*
import com.doyoumate.domain.auth.model.Certification
import com.doyoumate.domain.auth.model.RefreshToken
import com.doyoumate.domain.auth.repository.CertificationRepository
import com.doyoumate.domain.auth.repository.RefreshTokenRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.model.Student
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.core.JwtProvider
import com.github.jwt.security.JwtAuthentication
import net.nurigo.sdk.message.model.Message
import net.nurigo.sdk.message.request.SingleMessageSendingRequest
import net.nurigo.sdk.message.service.DefaultMessageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val studentRepository: StudentRepository,
    private val certificationRepository: CertificationRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val messageService: DefaultMessageService,
    @Value("\${coolsms.from}")
    private val from: String
) {
    fun sendCertification(request: SendCertificationRequest): Mono<Void> =
        with(request) {
            certificationRepository.findByStudentNumber(studentNumber)
                .flatMap { Mono.error<Student>(CertificationAlreadyExistException()) }
                .switchIfEmpty(Mono.defer {
                    studentRepository.findByNumber(studentNumber)
                        .switchIfEmpty(Mono.error(StudentNotFoundException()))
                })
                .filter { it.phoneNumber == to }
                .switchIfEmpty(Mono.error(InvalidCertificationException()))
                .filter { it.password.isNullOrBlank() }
                .switchIfEmpty(Mono.error(AccountAlreadyExistException()))
                .map {
                    Certification(
                        studentNumber = studentNumber,
                        code = List(6) { (0..9).random() }
                            .joinToString("") { it.toString() }
                    )
                }
                .flatMap { certificationRepository.save(it) }
                .map {
                    messageService.sendOne(
                        SingleMessageSendingRequest(
                            Message(
                                from = from,
                                to = to,
                                text = it.toMessage()
                            )
                        )
                    )!!
                }
                .then()
        }

    fun signUp(request: SignUpRequest): Mono<Void> =
        with(request) {
            certificationRepository.findByStudentNumber(certification.studentNumber)
                .filter { it == certification }
                .switchIfEmpty(Mono.error(InvalidCertificationException()))
                .flatMap {
                    Mono.zip(
                        studentRepository.findByNumber(certification.studentNumber)
                            .flatMap { studentRepository.save(it.copy(password = passwordEncoder.encode(password))) },
                        certificationRepository.deleteByStudentNumber(certification.studentNumber)
                    )
                }
                .then()
        }

    fun login(request: LoginRequest): Mono<LoginResponse> =
        with(request) {
            studentRepository.findByNumber(request.studentNumber)
                .switchIfEmpty(Mono.error(StudentNotFoundException()))
                .filter { it.password != null }
                .switchIfEmpty(Mono.error(AccountNotFoundException()))
                .filter { passwordEncoder.matches(password, it.password) }
                .switchIfEmpty(Mono.error(PasswordNotMatchedException()))
                .map {
                    JwtAuthentication(
                        id = it.id!!,
                        authorities = setOf(SimpleGrantedAuthority(it.role.name))
                    )
                }
                .flatMap {
                    Mono.zip(
                        Mono.just(it),
                        refreshTokenRepository.save(
                            RefreshToken(
                                studentId = it.id,
                                content = jwtProvider.createRefreshToken(it)
                            )
                        )
                    )
                }
                .map { (authentication, refreshToken) ->
                    LoginResponse(
                        accessToken = jwtProvider.createAccessToken(authentication),
                        refreshToken = refreshToken.content
                    )
                }
        }

    fun refresh(request: RefreshRequest): Mono<RefreshResponse> =
        with(request) {
            Mono.just(jwtProvider.getAuthentication(refreshToken))
                .onErrorResume { Mono.error(InvalidTokenException()) }
                .flatMap { authentication ->
                    refreshTokenRepository.findByStudentId(authentication.id)
                        .switchIfEmpty(Mono.error(TokenNotFoundException()))
                        .filter { it.content == refreshToken }
                        .switchIfEmpty(Mono.defer {
                            refreshTokenRepository.deleteByStudentId(authentication.id)
                                .then(Mono.error(InvalidAccessException()))
                        })
                        .flatMap {
                            refreshTokenRepository.save(
                                RefreshToken(
                                    studentId = it.studentId,
                                    content = jwtProvider.createRefreshToken(authentication)
                                )
                            )
                        }
                        .map {
                            RefreshResponse(
                                accessToken = jwtProvider.createAccessToken(authentication),
                                refreshToken = it.content
                            )
                        }
                }
        }

}
