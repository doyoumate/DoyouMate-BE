package com.doyoumate.domain.auth.dto.response

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String
)
