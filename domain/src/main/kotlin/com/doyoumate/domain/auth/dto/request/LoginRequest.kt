package com.doyoumate.domain.auth.dto.request

data class LoginRequest(
    val studentId: String,
    val password: String
)
