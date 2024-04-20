package com.doyoumate.api.board.service

import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.board.dto.request.CreateCommentRequest
import com.doyoumate.domain.board.dto.request.UpdateCommentRequest
import com.doyoumate.domain.board.dto.response.CommentResponse
import com.doyoumate.domain.board.exception.CommentNotFoundException
import com.doyoumate.domain.board.model.Comment
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val studentRepository: StudentRepository
) {
    fun createComment(request: CreateCommentRequest, authentication: JwtAuthentication): Mono<CommentResponse> =
        with(request) {
            studentRepository.findById(authentication.id)
                .flatMap {
                    commentRepository.save(
                        Comment(
                            writer = it,
                            content = content
                        )
                    )
                }
                .map { CommentResponse(it) }
        }

    fun updateCommentById(
        id: String,
        request: UpdateCommentRequest,
        authentication: JwtAuthentication
    ): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.save(request.updateEntity(it)) }
            .map { CommentResponse(it) }

    fun deleteCommentById(id: String, authentication: JwtAuthentication): Mono<Void> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.deleteById(id) }

    fun likeCommentById(id: String, authentication: JwtAuthentication): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .map {
                it.copy(likedUserIds = it.likedUserIds.toMutableSet()
                    .apply {
                        if (authentication.id in it.likedUserIds) {
                            remove(authentication.id)
                        } else {
                            add(authentication.id)
                        }
                    }
                    .toHashSet()
                )
            }
            .flatMap { commentRepository.save(it) }
            .map { CommentResponse(it) }
}
