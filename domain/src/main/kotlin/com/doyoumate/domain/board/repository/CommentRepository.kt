package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Comment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface CommentRepository : ReactiveMongoRepository<Comment, String> {
    fun findAllByPostId(postId: String): Flux<Comment>
}
