package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.global.util.sortBy
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.regex
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class CustomPostRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun search(
        boardId: String?,
        content: String,
        pageable: Pageable
    ): Flux<Post> =
        Query()
            .apply {
                boardId?.let { addCriteria(Criteria.where("board.id").isEqualTo(it)) }
                addCriteria(Criteria().orOperator(Post::title.regex(content, "i"), Post::content.regex(content, "i")))
                addCriteria(Post::deletedDate isEqualTo null)
                with(Post::createdDate sortBy Sort.Direction.DESC)
                with(pageable)
            }
            .let { mongoTemplate.find(it) }
}
