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
import com.doyoumate.domain.board.model.Writer
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.board.repository.PostRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val studentRepository: StudentRepository
) {
    fun getCommentsByPostId(postId: String): Flux<CommentResponse> =
        commentRepository.findAllByPostIdAndDeletedDateIsNullOrderByCreatedDateAsc(postId)
            .map { CommentResponse(it) }

    fun getCommentsByWriterId(writerId: String): Flux<CommentResponse> =
        commentRepository.findAllByWriterIdAndDeletedDateIsNullOrderByCreatedDateDesc(writerId)
            .map { CommentResponse(it) }

    @Transactional
    fun createComment(request: CreateCommentRequest, authentication: DefaultJwtAuthentication): Mono<CommentResponse> =
        with(request) {
            studentRepository.findById(authentication.id)
                .switchIfEmpty(Mono.error(StudentNotFoundException()))
                .zipWhen {
                    postRepository.findByIdAndDeletedDateIsNull(postId)
                        .switchIfEmpty(Mono.error(PostNotFoundException()))
                }
                .flatMap { (student, post) ->
                    commentRepository.save(
                        Comment(
                            postId = postId,
                            writer = Writer(student),
                            content = content
                        )
                    ).flatMap {
                        postRepository.save(post.copy(commentIds = post.commentIds.apply { add(it.id!!) }))
                            .thenReturn(it)
                    }
                }
                .map { CommentResponse(it) }
        }

    fun updateCommentById(
        id: String,
        request: UpdateCommentRequest,
        authentication: DefaultJwtAuthentication
    ): Mono<CommentResponse> =
        commentRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { commentRepository.save(request.updateEntity(it)) }
            .map { CommentResponse(it) }

    @Transactional
    fun deleteCommentById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        commentRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(CommentNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { comment ->
                commentRepository.save(comment.copy(deletedDate = LocalDateTime.now()))
                    .zipWith(
                        postRepository.findByIdAndDeletedDateIsNull(comment.postId)
                            .flatMap { postRepository.save(it.copy(commentIds = it.commentIds.apply { remove(id) })) }
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
