package com.doyoumate.api.board.service

import com.doyoumate.api.global.s3.S3Provider
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@Service
class PostService(
    private val postRepository: PostRepository,
    private val customPostRepository: CustomPostRepository,
    private val commentRepository: CommentRepository,
    private val studentRepository: StudentRepository,
    private val boardRepository: BoardRepository,
    private val s3Provider: S3Provider
) {
    fun getPostById(id: String): Mono<PostResponse> =
        postRepository.findByIdAndDeletedDateIsNull(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .map { PostResponse(it) }

    fun getPostPageByWriterId(writerId: String, lastCreatedDate: LocalDateTime?, pageSize: Int): Flux<PostResponse> =
        customPostRepository.getPageByWriterId(writerId, lastCreatedDate, pageSize)
            .map { PostResponse(it) }

    fun getLikedPostPageByStudentId(
        studentId: String,
        lastCreatedDate: LocalDateTime?,
        size: Int
    ): Flux<PostResponse> =
        customPostRepository.getPageByLikedStudentIdsIn(studentId, lastCreatedDate, size)
            .map { PostResponse(it) }

    fun getPopularPosts(): Flux<PostResponse> =
        postRepository.findTop2OrderByLikedStudentIdsSizeAndDeletedDateIsNull()
            .map { PostResponse(it) }

    fun searchPostPage(
        boardId: String?,
        content: String,
        lastCreatedDate: LocalDateTime?,
        size: Int
    ): Flux<PostResponse> =
        customPostRepository.searchPage(boardId, content, lastCreatedDate, size)
            .map { PostResponse(it) }

    @Transactional
    fun createPost(request: CreatePostRequest, authentication: DefaultJwtAuthentication): Mono<PostResponse> =
        with(request) {
            boardRepository.findById(boardId)
                .switchIfEmpty(Mono.error(BoardNotFoundException()))
                .zipWhen {
                    studentRepository.findById(authentication.id)
                        .switchIfEmpty(Mono.error(StudentNotFoundException()))
                }
                .flatMap { (board, student) ->
                    postRepository.save(
                        Post(
                            board = board,
                            writer = Writer(student),
                            title = title,
                            content = content,
                            images = emptyList()
                        )
                    )
                }
                .flatMap { post ->
                    Flux.mergeSequential(images.map { image ->
                        s3Provider.upload(createObjectKey(post.id!!), image)
                    }).collectList()
                        .defaultIfEmpty(emptyList())
                        .flatMap { postRepository.save(post.copy(images = it)) }
                }
                .map { PostResponse(it) }
        }

    fun updatePostById(
        id: String,
        request: UpdatePostRequest,
        authentication: DefaultJwtAuthentication
    ): Mono<PostResponse> =
        with(request) {
            postRepository.findByIdAndDeletedDateIsNull(id)
                .switchIfEmpty(Mono.error(PostNotFoundException()))
                .filter { it.writer.id == authentication.id }
                .switchIfEmpty(Mono.error(PermissionDeniedException()))
                .flatMap { post ->
                    val mono = Mono.justOrEmpty(isImageUpdated.takeIf { it })

                    Mono.zip(
                        mono.flatMap { s3Provider.deleteAll(post.images.map { URI.create(it) }) }
                            .thenReturn(true),
                        mono.flatMap {
                            Flux.mergeSequential(images.map { s3Provider.upload(createObjectKey(id), it) })
                                .collectList()
                                .defaultIfEmpty(emptyList())
                        }.defaultIfEmpty(post.images),
                        boardRepository.findById(boardId)
                    ).flatMap { (_, images, board) -> postRepository.save(updateEntity(post, board, images)) }
                }
                .map { PostResponse(it) }
        }

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

    private fun createObjectKey(id: String) = "images/${id}/${UUID.randomUUID()}"
}
