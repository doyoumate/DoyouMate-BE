package com.doyoumate.api.board.service

import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.board.dto.request.CreateCommentRequest
import com.doyoumate.domain.board.dto.request.UpdateCommentRequest
import com.doyoumate.domain.board.dto.response.CommentResponse
import com.doyoumate.domain.board.exception.CommentNotFoundException
import com.doyoumate.domain.board.model.Comment
import com.doyoumate.domain.board.repository.CommentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {
    fun getCommentById(id: String): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .map { CommentResponse(it) }

    fun getCommentsByPostId(postId: String): Flux<CommentResponse> =
        commentRepository.findAllByPostId(postId)
            .map { CommentResponse(it) }

    fun createComment(request: CreateCommentRequest, authentication: JwtAuthentication): Mono<CommentResponse> =
        with(request) {
            commentRepository.save(
                Comment(
                    postId = postId,
                    writerId = authentication.id,
                    content = content
                )
            ).map { CommentResponse(it) }
        }

    fun updateCommentById(
        id: String,
        request: UpdateCommentRequest,
        authentication: JwtAuthentication
    ): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writerId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.save(request.updateEntity(it)) }
            .map { CommentResponse(it) }

    fun deleteCommentById(id: String, authentication: JwtAuthentication): Mono<Void> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writerId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.deleteById(id) }
}
