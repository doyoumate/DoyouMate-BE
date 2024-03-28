package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String> {
    fun findAllByBoardId(boardId: String): Flux<Post>
}
