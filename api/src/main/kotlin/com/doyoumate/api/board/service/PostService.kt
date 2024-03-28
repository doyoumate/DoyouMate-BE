package com.doyoumate.api.board.service

import com.doyoumate.domain.auth.exception.PermissionDeniedException
import com.doyoumate.domain.board.dto.request.CreatePostRequest
import com.doyoumate.domain.board.dto.request.UpdatePostRequest
import com.doyoumate.domain.board.dto.response.PostResponse
import com.doyoumate.domain.board.exception.PostNotFoundException
import com.doyoumate.domain.board.model.Post
import com.doyoumate.domain.board.repository.PostRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PostService(
    private val postRepository: PostRepository
) {
    fun getPostById(id: String): Mono<PostResponse> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .map { PostResponse(it) }

    fun getPostsByBoardId(boardId: String): Flux<PostResponse> =
        postRepository.findAllByBoardId(boardId)
            .map { PostResponse(it) }

    fun createPost(request: CreatePostRequest, authentication: JwtAuthentication): Mono<PostResponse> =
        with(request) {
            postRepository.save(
                Post(
                    boardId = boardId,
                    writerId = authentication.id,
                    title = title,
                    content = content,
                    likedUserIds = emptyList(),
                )
            ).map { PostResponse(it) }
        }

    fun updatePostById(id: String, request: UpdatePostRequest, authentication: JwtAuthentication): Mono<PostResponse> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writerId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { postRepository.save(request.updateEntity(it)) }
            .map { PostResponse(it) }

    fun deletePostById(id: String, authentication: JwtAuthentication): Mono<Void> =
        postRepository.findById(id)
            .switchIfEmpty(Mono.error(PostNotFoundException()))
            .filter { it.writerId == authentication.id }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { postRepository.deleteById(id) }
}
