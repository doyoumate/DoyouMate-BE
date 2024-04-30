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
import com.doyoumate.domain.board.repository.BoardRepository
import com.doyoumate.domain.board.repository.CustomPostRepository
import com.doyoumate.domain.board.repository.PostRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PostService(
    private val postRepository: PostRepository,
    private val customPostRepository: CustomPostRepository,
    private val studentRepository: StudentRepository,
    private val boardRepository: BoardRepository,
) {
    fun getPostsByStudentId(studentId: String): Flux<PostResponse> =
        postRepository.findAllByWriterIdBOrderByCreatedDateBoardDesc(studentId)
            .map { PostResponse(it) }

    fun getPopularPosts(): Flux<PostResponse> =
        postRepository.findTop2OrderByLikedUserIdsSize()
            .map { PostResponse(it) }

    fun searchPosts(boardId: String?, content: String, pageable: Pageable): Flux<PostResponse> =
        customPostRepository.search(boardId, content, pageable)
            .map { PostResponse(it) }

    fun createPost(request: CreatePostRequest, authentication: JwtAuthentication): Mono<PostResponse> =
        with(request) {
            Mono.zip(
                boardRepository.findById(boardId)
                    .switchIfEmpty(Mono.error(BoardNotFoundException())),
                studentRepository.findById(authentication.id)
                    .switchIfEmpty(Mono.error(StudentNotFoundException()))
            ).flatMap { (board, student) ->
                postRepository.save(
                    Post(
                        board = board,
                        writer = student,
                        title = title,
                        content = content
                    )
                )
            }.map { PostResponse(it) }
        }

    fun updatePostById(id: String, request: UpdatePostRequest, authentication: JwtAuthentication): Mono<PostResponse> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { postRepository.save(request.updateEntity(it)) }
            .map { PostResponse(it) }

    fun deletePostById(id: String, authentication: JwtAuthentication): Mono<Void> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writer.id == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { postRepository.deleteById(id) }

    fun likePostById(id: String, authentication: JwtAuthentication): Mono<PostResponse> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
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
            .flatMap { postRepository.save(it) }
            .map { PostResponse(it) }
}
