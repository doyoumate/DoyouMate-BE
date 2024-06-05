package com.doyoumate.api.board.handler

import com.doyoumate.api.board.service.PostService
import com.doyoumate.api.global.config.getAuthentication
import com.doyoumate.common.annotation.Handler
import com.doyoumate.common.util.component1
import com.doyoumate.common.util.getQueryParam
import com.doyoumate.domain.board.dto.request.CreatePostRequest
import com.doyoumate.domain.board.dto.request.UpdatePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Handler
class PostHandler(
    private val postService: PostService
) {
    fun getPostById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(postService.getPostById(request.pathVariable("id")))

    fun getMyPosts(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(postService.getPostsByWriterId(it.id))
            }

    fun getLikedPosts(request: ServerRequest): Mono<ServerResponse> =
        request.getAuthentication()
            .flatMap {
                ServerResponse.ok()
                    .body(postService.getLikedPostsByStudentId(it.id))
            }

    fun getPopularPosts(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(postService.getPopularPosts())

    fun searchPosts(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            ServerResponse.ok()
                .body(
                    postService.searchPosts(
                        getQueryParam("boardId"),
                        getQueryParam("content")!!,
                        getQueryParam<String>("lastCreatedDate")?.let {
                            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        },
                        getQueryParam("size")!!
                    )
                )
        }

    fun createPost(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            multipartData()
                .map { CreatePostRequest(it) }
                .zipWith(getAuthentication())
                .flatMap { (createPostRequest, authentication) ->
                    ServerResponse.ok()
                        .body(postService.createPost(createPostRequest, authentication))
                }
        }

    fun updatePostById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            multipartData()
                .map { UpdatePostRequest(it) }
                .zipWith(getAuthentication())
                .flatMap { (updatePostRequest, authentication) ->
                    ServerResponse.ok()
                        .body(postService.updatePostById(pathVariable("id"), updatePostRequest, authentication))
                }
        }

    fun deletePostById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(postService.deletePostById(pathVariable("id"), it))
                }
        }

    fun likePostById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            getAuthentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(postService.likePostById(pathVariable("id"), it))
                }
        }
}
