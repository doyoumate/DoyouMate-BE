package com.doyoumate.api.board.service

import com.doyoumate.common.util.component1
import com.doyoumate.domain.board.dto.request.CreateBoardRequest
import com.doyoumate.domain.board.dto.request.UpdateBoardRequest
import com.doyoumate.domain.board.dto.response.BoardResponse
import com.doyoumate.domain.board.exception.BoardNotFoundException
import com.doyoumate.domain.board.repository.BoardRepository
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.board.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {
    fun getBoards(): Flux<BoardResponse> =
        boardRepository.findAllByDeletedDateIsNull()
            .map { BoardResponse(it) }

    fun createBoard(request: CreateBoardRequest): Mono<BoardResponse> =
        boardRepository.save(request.toEntity())
            .map { BoardResponse(it) }

    @Transactional
    fun updateBoardById(id: String, request: UpdateBoardRequest): Mono<BoardResponse> =
        boardRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(BoardNotFoundException()))
            .map { request.updateEntity(it) }
            .flatMap { board ->
                boardRepository.save(board)
                    .zipWith(
                        postRepository.findAllByBoardId(board.id!!)
                            .flatMap { postRepository.save(it.copy(board = board)) }
                            .then(Mono.just(true))
                    )
            }
            .map { (board) -> BoardResponse(board) }

    @Transactional
    fun deleteBoardById(id: String): Mono<Void> =
        with(LocalDateTime.now()) {
            boardRepository.findByIdAndDeletedDateIsNull(id)
                .switchIfEmpty(Mono.error(BoardNotFoundException()))
                .flatMap { boardRepository.save(it.copy(deletedDate = this)) }
                .zipWith(
                    postRepository.findAllByBoardIdAndDeletedDateIsNull(id)
                        .flatMap { post ->
                            postRepository.save(post.copy(deletedDate = this))
                                .zipWith(
                                    commentRepository.findAllByPostIdAndDeletedDateIsNull(id)
                                        .map { it.copy(deletedDate = this) }
                                        .flatMap { commentRepository.save(it) }
                                        .then(Mono.just(true))
                                )
                        }
                        .then(Mono.just(true))
                )
                .then()
        }
}
