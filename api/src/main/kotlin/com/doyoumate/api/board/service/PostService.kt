package com.doyoumate.api.board.service

import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.board.dto.request.CreatePostRequest
import com.doyoumate.domain.board.dto.request.UpdatePostRequest
import com.doyoumate.domain.board.dto.response.PostResponse
import com.doyoumate.domain.board.exception.BoardNotFoundException
import com.doyoumate.domain.board.exception.PostNotFoundException
import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.board.model.Writer
import com.doyoumate.domain.board.repository.BoardRepository
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.board.repository.CustomPostRepository
import com.doyoumate.domain.board.repository.PostRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class PostService(
    private val postRepository: PostRepository,
    private val customPostRepository: CustomPostRepository,
    private val commentRepository: CommentRepository,
    private val studentRepository: StudentRepository,
    private val boardRepository: BoardRepository,
) {
    fun getPostById(id: String): Mono<PostResponse> =
        postRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .map { PostResponse(it) }

    fun getPostsByWriterId(writerId: String): Flux<PostResponse> =
        postRepository.findAllByWriterIdAndDeletedDateIsNullOrderByCreatedDateDesc(writerId)
            .map { PostResponse(it) }

    fun getLikedPostsByStudentId(studentId: String): Flux<PostResponse> =
        postRepository.findAllByLikedStudentIdsContainsAndDeletedDateIsNullOrderByCreatedDateDesc(studentId)
            .map { PostResponse(it) }

    fun getPopularPosts(): Flux<PostResponse> =
        postRepository.findTop2OrderByLikedStudentIdsSizeAndDeletedDateIsNull()
            .map { PostResponse(it) }

    fun searchPosts(boardId: String?, content: String, pageable: Pageable): Flux<PostResponse> =
        customPostRepository.search(boardId, content, pageable)
            .map { PostResponse(it) }

    fun createPost(request: CreatePostRequest, authentication: DefaultJwtAuthentication): Mono<PostResponse> =
        with(request) {
            boardRepository.findById(boardId)
                .switchIfEmpty(Mono.error(BoardNotFoundException()))
                .zipWith(
                    studentRepository.findById(authentication.id)
                        .switchIfEmpty(Mono.error(StudentNotFoundException()))
                )
                .flatMap { (board, student) ->
                    postRepository.save(
                        Post(
                            board = board,
                            writer = Writer(student),
                            title = title,
                            content = content
                        )
                    )
                }
                .map { PostResponse(it) }
        }

    fun updatePostById(
        id: String,
        request: UpdatePostRequest,
        authentication: DefaultJwtAuthentication
    ): Mono<PostResponse> =
        postRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { postRepository.save(request.updateEntity(it)) }
            .map { PostResponse(it) }

    @Transactional
    fun deletePostById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        postRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap {
                LocalDateTime.now()
                    .let { now ->
                        postRepository.save(it.copy(deletedDate = now))
                            .zipWith(
                                commentRepository.findAllByPostIdAndDeletedDateIsNull(id)
                                    .map { it.copy(deletedDate = now) }
                                    .flatMap { commentRepository.save(it) }
                                    .then(Mono.just(true))
                            )
                    }
            }
            .then()

    fun likePostById(id: String, authentication: DefaultJwtAuthentication): Mono<PostResponse> =
        postRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
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
            .flatMap { postRepository.save(it) }
            .map { PostResponse(it) }
}
