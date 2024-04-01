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
    fun findByStudentId(studentId: String): Mono<Certification> =
        redisTemplate.opsForValue()
            .get(studentId.toKey())
            .map {
                Certification(
                    studentId = studentId,
                    code = it
                )
            }

    fun save(certification: Certification): Mono<Certification> =
        with(certification) {
            redisTemplate.opsForValue()
                .set(studentId.toKey(), code, Duration.ofMinutes(expire))
                .thenReturn(this)
        }

    fun deleteByStudentId(studentId: String): Mono<Boolean> =
        redisTemplate.opsForValue()
            .delete(studentId.toKey())

    private fun String.toKey(): String = "certification:$this"
}
