package com.doyoumate.api.board.handler

import com.doyoumate.api.board.service.BoardService
import com.doyoumate.common.annotation.Handler
import com.doyoumate.domain.board.dto.request.CreateBoardRequest
import com.doyoumate.domain.board.dto.request.UpdateBoardRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class BoardHandler(
    private val boardService: BoardService
) {
    fun getBoards(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(boardService.getBoards())

    fun createBoard(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<CreateBoardRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(boardService.createBoard(it))
            }

    fun updateBoardById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<UpdateBoardRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(boardService.updateBoardById(pathVariable("id"), it))
                }
        }

    fun deleteBoardById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(boardService.deleteBoardById(request.pathVariable("id")))
}
