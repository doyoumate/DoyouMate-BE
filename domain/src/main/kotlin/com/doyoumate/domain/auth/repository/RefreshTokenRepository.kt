package com.doyoumate.domain.auth.repository

import com.doyoumate.domain.auth.model.RefreshToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class RefreshTokenRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${jwt.refreshTokenExpire}")
    private val expire: Long
) {
    fun findByStudentId(studentId: String): Mono<RefreshToken> =
        redisTemplate.opsForValue()
            .get(studentId.toKey())
            .map {
                RefreshToken(
                    studentId = studentId,
                    content = it
                )
            }

    fun save(refreshToken: RefreshToken): Mono<RefreshToken> =
        with(refreshToken) {
            redisTemplate.opsForValue()
                .set(studentId.toKey(), content, Duration.ofMinutes(expire))
                .thenReturn(this)
        }

    fun deleteByStudentId(studentId: String): Mono<Boolean> =
        redisTemplate.opsForValue()
            .delete(studentId.toKey())

    private fun String.toKey(): String = "refreshToken:$this"
}
