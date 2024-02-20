package com.doyoumate.api.auth.service

import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import com.doyoumate.domain.auth.exception.AccountAlreadyExistException
import com.doyoumate.domain.auth.exception.CertificationAlreadyExistException
import com.doyoumate.domain.auth.exception.InvalidCertificationException
import com.doyoumate.domain.auth.exception.StudentNotFoundException
import com.doyoumate.domain.auth.model.Certification
import com.doyoumate.domain.auth.repository.CertificationRepository
import com.doyoumate.domain.student.model.Student
import com.doyoumate.domain.student.repository.StudentRepository
import net.nurigo.sdk.message.model.Message
import net.nurigo.sdk.message.request.SingleMessageSendingRequest
import net.nurigo.sdk.message.service.DefaultMessageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val studentRepository: StudentRepository,
    private val certificationRepository: CertificationRepository,
    private val messageService: DefaultMessageService,
    @Value("\${coolsms.from}")
    private val from: String
) {
    fun sendCertification(request: SendCertificationRequest): Mono<Void> =
        with(request) {
            certificationRepository.findByStudentId(studentId)
                .flatMap { Mono.error<Student>(CertificationAlreadyExistException()) }
                .switchIfEmpty(Mono.defer {
                    studentRepository.findById(studentId)
                        .switchIfEmpty(Mono.error(StudentNotFoundException()))
                })
                .filter { it.phoneNumber == to }
                .switchIfEmpty(Mono.error(InvalidCertificationException()))
                .filter { it.password.isNullOrBlank() }
                .switchIfEmpty(Mono.error(AccountAlreadyExistException()))
                .map {
                    Certification(
                        studentId = studentId,
                        code = List(6) { (0..9).random() }
                            .joinToString("") { it.toString() }
                    )
                }
                .flatMap {
                    certificationRepository.save(it)
                }
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
}
