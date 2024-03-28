package com.doyoumate.api.board.handler

import com.doyoumate.api.board.service.CommentService
import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.common.annotation.Handler
import com.doyoumate.common.util.component1
import com.doyoumate.domain.board.dto.request.CreateCommentRequest
import com.doyoumate.domain.board.dto.request.UpdateCommentRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component2

@Handler
class CommentHandler(
    private val commentService: CommentService
) {
    fun getCommentById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(commentService.getCommentById(request.pathVariable("id")))

    fun getCommentsByPostId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(commentService.getCommentsByPostId(request.pathVariable("postId")))

    fun createComment(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(bodyToMono<CreateCommentRequest>(), getAuthentication())
                .flatMap { (createCommentRequest, authentication) ->
                    ServerResponse.ok()
                        .body(commentService.createComment(createCommentRequest, authentication))
                }
        }

    fun updateCommentById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(bodyToMono<UpdateCommentRequest>(), getAuthentication())
                .flatMap { (updateCommentRequest, authentication) ->
                    ServerResponse.ok()
                        .body(
                            commentService.updateCommentById(
                                pathVariable("id"),
                                updateCommentRequest,
                                authentication
                            )
                        )
                }
        }

    fun deleteCommentById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(commentService.deleteCommentById(pathVariable("id"), it))
                }
        }
}
