package com.doyoumate.domain.auth.repository

import com.doyoumate.domain.auth.model.Certification
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class CertificationRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${expire}")
    private val expire: Long
) {
    fun findByStudentNumber(studentNumber: String): Mono<Certification> =
        redisTemplate.opsForValue()
            .get(studentNumber.toKey())
            .map {
                Certification(
                    studentNumber = studentNumber,
                    code = it
                )
            }

    fun save(certification: Certification): Mono<Certification> =
        with(certification) {
            redisTemplate.opsForValue()
                .set(studentNumber.toKey(), code, Duration.ofMinutes(expire))
                .thenReturn(this)
        }

    fun deleteByStudentNumber(studentNumber: String): Mono<Boolean> =
        redisTemplate.opsForValue()
            .delete(studentNumber.toKey())

    private fun String.toKey(): String = "certification:$this"
}
