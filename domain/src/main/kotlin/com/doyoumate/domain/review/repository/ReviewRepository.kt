package com.doyoumate.domain.review.repository

import com.doyoumate.domain.review.model.Review
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ReviewRepository : ReactiveMongoRepository<Review, String> {
    fun findAllByLectureId(lectureId: String): Flux<Review>

    fun findAllByStudentId(studentId: String): Flux<Review>
}
