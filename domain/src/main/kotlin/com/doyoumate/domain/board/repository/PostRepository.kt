package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String> {
    @Query("{ 'board.id' : ?0 }")
    fun findAllByBoardId(boardId: String): Flux<Post>

    @Query("{ 'writer.id' : ?0 }")
    fun findAllByWriterId(writerId: String): Flux<Post>
}
