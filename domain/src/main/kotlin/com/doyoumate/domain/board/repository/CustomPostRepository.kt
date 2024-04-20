package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.global.util.query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
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
        query {
            "board.id" isEqualTo boardId
            "content" like content
            "createdDate" sortBy Sort.Direction.DESC
            paging(pageable)
        }.let {
            mongoTemplate.find(it)
        }
}
