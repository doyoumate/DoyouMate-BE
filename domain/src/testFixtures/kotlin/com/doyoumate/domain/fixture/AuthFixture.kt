package com.doyoumate.domain.fixture

import com.doyoumate.domain.auth.dto.request.LoginRequest
import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import com.doyoumate.domain.auth.dto.request.SignUpRequest
import com.doyoumate.domain.auth.dto.response.LoginResponse
import com.doyoumate.domain.auth.model.Certification
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

const val CODE = "123456"
const val FROM = "01012345678"
val passwordEncoder = BCryptPasswordEncoder()

fun createCertification(
    studentId: String = ID,
    code: String = CODE
): Certification =
    Certification(
        studentId = studentId,
        code = code
    )

fun createSendCertificationRequest(
    studentId: String = ID,
    to: String = PHONE_NUMBER
): SendCertificationRequest =
    SendCertificationRequest(
        studentId = studentId,
        to = to
    )

fun createSignUpRequest(
    certification: Certification = createCertification(),
    password: String = ROW_PASSWORD
): SignUpRequest =
    SignUpRequest(
        certification = certification,
        password = password
    )

fun createLoginRequest(
    studentId: String = ID,
    password: String = ROW_PASSWORD
): LoginRequest =
    LoginRequest(
        studentId = studentId,
        password = password
    )

fun createLoginResponse(
    accessToken: String = ACCESS_TOKEN,
    refreshToken: String = REFRESH_TOKEN
): LoginResponse =
    LoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
