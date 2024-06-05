package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.global.util.sortBy
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class CustomPostRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun search(
        boardId: String?,
        content: String,
        lastCreatedDate: LocalDateTime?,
        size: Int
    ): Flux<Post> =
        Query()
            .apply {
                boardId?.let { addCriteria(Criteria.where("board.id").isEqualTo(it)) }
                addCriteria(Criteria().orOperator(Post::title.regex(content, "i"), Post::content.regex(content, "i")))
                addCriteria(Post::deletedDate isEqualTo null)
                lastCreatedDate?.let { addCriteria(Post::createdDate lt lastCreatedDate) }
                with(Post::createdDate sortBy Sort.Direction.DESC)
                limit(size)
            }
            .let { mongoTemplate.find(it) }
}
