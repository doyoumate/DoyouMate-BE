package com.doyoumate.domain.fixture

import com.doyoumate.domain.auth.dto.request.SendCertificationRequest
import com.doyoumate.domain.auth.dto.request.SignUpRequest
import com.doyoumate.domain.auth.model.Certification

const val CODE = "123456"
const val FROM = "01012345678"

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
    password: String = PASSWORD
): SignUpRequest =
    SignUpRequest(
        certification = certification,
        password = password
    )
