package com.doyoumate.domain.board.repository

import com.doyoumate.domain.board.model.Board
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository : ReactiveMongoRepository<Board, String>
