package com.doyoumate.domain.auth.dto.request

data class LoginRequest(
    val studentNumber: String,
    val password: String
)
