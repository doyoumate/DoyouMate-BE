package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String> {
    @Query("{ 'board.id' : ?0 }")
    fun findAllByBoardId(boardId: String): Flux<Post>

    @Query("{ 'writer.id' : ?0 }")
    fun findAllByWriterIdOrderByCreatedDateBoardDesc(writerId: String): Flux<Post>

    fun findAllByLikedStudentIdsContains(studentId: String): Flux<Post>

    @Aggregation(
        pipeline = [
            "{ \$addFields: { count: { \$size: '\$likedStudentIds' } }}",
            "{ \$sort: { count: -1 } }",
            "{ \$limit: 2 }"
        ]
    )
    fun findTop2OrderByLikedStudentIdsSize(): Flux<Post>
}
