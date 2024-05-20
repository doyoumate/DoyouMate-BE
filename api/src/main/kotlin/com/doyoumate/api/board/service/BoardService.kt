package com.doyoumate.api.board.service

import com.doyoumate.common.util.component1
import com.doyoumate.domain.board.dto.request.CreateBoardRequest
import com.doyoumate.domain.board.dto.request.UpdateBoardRequest
import com.doyoumate.domain.board.dto.response.BoardResponse
import com.doyoumate.domain.board.exception.BoardNotFoundException
import com.doyoumate.domain.board.repository.BoardRepository
import com.doyoumate.domain.board.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository
) {
    fun getBoards(): Flux<BoardResponse> =
        boardRepository.findAll()
            .map { BoardResponse(it) }

    fun createBoard(request: CreateBoardRequest): Mono<BoardResponse> =
        boardRepository.save(request.toEntity())
            .map { BoardResponse(it) }

    @Transactional
    fun updateBoardById(id: String, request: UpdateBoardRequest): Mono<BoardResponse> =
        boardRepository.findById(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .map { request.updateEntity(it) }
            .flatMap { board ->
                Mono.zip(
                    boardRepository.save(board),
                    postRepository.findAllByBoardId(board.id!!)
                        .flatMap { postRepository.save(it.copy(board = board)) }
                        .collectList()
                )
            }
            .map { (board) -> BoardResponse(board) }

    @Transactional
    fun deleteBoardById(id: String): Mono<Void> =
        boardRepository.findById(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .flatMap { boardRepository.deleteById(id) }
}
