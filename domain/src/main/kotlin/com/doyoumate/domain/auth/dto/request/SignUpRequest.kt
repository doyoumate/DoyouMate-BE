package com.doyoumate.domain.auth.dto.request

data class SignUpRequest(
    val studentNumber: String,
    val code: String,
    val password: String
)
