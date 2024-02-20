package com.doyoumate.domain.auth.dto.request

import com.doyoumate.domain.auth.model.Certification

data class SignUpRequest(
    val certification: Certification,
    val password: String
)
