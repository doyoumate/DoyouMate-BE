package com.doyoumate.api.board.service

import com.doyoumate.domain.board.dto.request.CreateBoardRequest
import com.doyoumate.domain.board.dto.request.UpdateBoardRequest
import com.doyoumate.domain.board.dto.response.BoardResponse
import com.doyoumate.domain.board.exception.BoardNotFoundException
import com.doyoumate.domain.board.repository.BoardRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {
    fun getBoardById(id: String): Mono<BoardResponse> =
        boardRepository.findById(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .map { BoardResponse(it) }

    fun getBoards(): Flux<BoardResponse> =
        boardRepository.findAll()
            .map { BoardResponse(it) }

    fun createBoard(request: CreateBoardRequest): Mono<BoardResponse> =
        boardRepository.save(request.toEntity())
            .map { BoardResponse(it) }

    fun updateBoardById(id: String, request: UpdateBoardRequest): Mono<BoardResponse> =
        boardRepository.findById(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .flatMap { boardRepository.save(request.updateEntity(it)) }
            .map { BoardResponse(it) }

    fun deleteBoardById(id: String): Mono<Void> =
        boardRepository.findById(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .flatMap { boardRepository.deleteById(id) }
}
