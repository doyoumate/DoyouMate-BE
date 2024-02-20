package com.doyoumate.domain.auth.dto.request

data class SendCertificationRequest(
    val studentId: String,
    val to: String
)
