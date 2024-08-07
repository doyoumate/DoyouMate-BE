package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Board
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface BoardRepository : ReactiveMongoRepository<Board, String> {
    fun findByIdAndDeletedDateIsNull(id: String): Mono<Board>

    fun findAllByDeletedDateIsNull(): Flux<Board>
}
