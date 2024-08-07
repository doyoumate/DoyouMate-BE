package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Comment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CommentRepository : ReactiveMongoRepository<Comment, String> {
    fun findByIdAndDeletedDateIsNull(id: String): Mono<Comment>

    fun findAllByWriterId(writerId: String): Flux<Comment>

    fun findAllByPostIdAndDeletedDateIsNull(postId: String): Flux<Comment>

    fun findAllByCommentIdAndDeletedDateIsNull(commentId: String): Flux<Comment>

    fun findAllByPostIdOrderByCreatedDateAsc(postId: String): Flux<Comment>

    fun findAllByWriterIdAndDeletedDateIsNullOrderByCreatedDateDesc(writerId: String): Flux<Comment>
}
