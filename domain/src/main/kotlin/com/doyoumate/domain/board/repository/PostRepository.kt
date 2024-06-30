package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String> {
    fun findByIdAndDeletedDateIsNull(id: String): Mono<Post>

    fun findAllByWriterId(writerId: String): Flux<Post>

    fun findAllByBoardId(boardId: String): Flux<Post>

    fun findAllByBoardIdAndDeletedDateIsNull(boardId: String): Flux<Post>

    @Aggregation(
        "{ \$match: { deletedDate: null } }",
        "{ \$addFields: { count: { \$size: '\$likedStudentIds' } }}",
        "{ \$sort: { count: -1 } }",
        "{ \$limit: 2 }"
    )
    fun findTop2OrderByLikedStudentIdsSizeAndDeletedDateIsNull(): Flux<Post>
}
