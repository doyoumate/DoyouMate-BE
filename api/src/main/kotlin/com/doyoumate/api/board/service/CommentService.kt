package com.doyoumate.api.board.service

import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.board.dto.request.CreateCommentRequest
import com.doyoumate.domain.board.dto.request.UpdateCommentRequest
import com.doyoumate.domain.board.dto.response.CommentResponse
import com.doyoumate.domain.board.exception.CommentNotFoundException
import com.doyoumate.domain.board.exception.PostNotFoundException
import com.doyoumate.domain.board.model.Comment
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.board.repository.PostRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val studentRepository: StudentRepository
) {
    fun getCommentsByPostId(postId: String): Flux<CommentResponse> =
        commentRepository.findAllByPostIdOrderByCreatedDateAsc(postId)
            .map { CommentResponse(it) }

    fun getCommentsByWriterId(writerId: String): Flux<CommentResponse> =
        commentRepository.findAllByWriterIdOrderByCreatedDateDesc(writerId)
            .map { CommentResponse(it) }

    fun createComment(request: CreateCommentRequest, authentication: DefaultJwtAuthentication): Mono<CommentResponse> =
        with(request) {
            Mono.zip(
                studentRepository.findById(authentication.id)
                    .switchIfEmpty(Mono.error(StudentNotFoundException())),
                postRepository.findById(postId)
                    .switchIfEmpty(Mono.error(PostNotFoundException()))
            ).flatMap { (student, post) ->
                commentRepository.save(
                    Comment(
                        postId = postId,
                        writer = student,
                        content = content
                    )
                ).flatMap {
                    postRepository.save(post.copy(commentIds = post.commentIds.apply { add(it.id!!) }))
                        .thenReturn(it)
                }
            }.map { CommentResponse(it) }
        }

    fun updateCommentById(
        id: String,
        request: UpdateCommentRequest,
        authentication: DefaultJwtAuthentication
    ): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.save(request.updateEntity(it)) }
            .map { CommentResponse(it) }

    fun deleteCommentById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { comment ->
                Mono.zip(
                    postRepository.findById(comment.postId)
                        .flatMap {
                            postRepository.save(it.copy(commentIds = it.commentIds.apply { remove(id) }))
                                .thenReturn(true)
                        },
                    commentRepository.deleteById(id)
                        .thenReturn(true)
                )
            }
            .then()

    fun likeCommentById(id: String, authentication: DefaultJwtAuthentication): Mono<CommentResponse> =
        commentRepository.findById(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .map {
                it.copy(likedStudentIds = it.likedStudentIds.toMutableSet()
                    .apply {
                        if (authentication.id in it.likedStudentIds) {
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
