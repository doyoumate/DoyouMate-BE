package com.doyoumate.domain.auth.model

import org.springframework.data.redis.core.RedisHash

@RedisHash
data class RefreshToken(
    val studentId: String,
    val content: String
)
