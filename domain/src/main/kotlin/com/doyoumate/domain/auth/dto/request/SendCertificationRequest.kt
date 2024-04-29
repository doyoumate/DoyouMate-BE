package com.doyoumate.domain.auth.dto.request

data class SendCertificationRequest(
    val studentNumber: String,
    val to: String
)
