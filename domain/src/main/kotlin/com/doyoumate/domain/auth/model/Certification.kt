package com.doyoumate.domain.auth.model

import org.springframework.data.redis.core.RedisHash

@RedisHash
data class Certification(
    val studentId: String,
    val code: String
) {
    fun toMessage(): String = "[DoyouMate] 인증번호는 [$code] 입니다."
}
