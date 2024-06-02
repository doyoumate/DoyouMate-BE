package com.doyoumate.api.board.router

import com.doyoumate.api.board.handler.PostHandler
import com.doyoumate.common.annotation.Router
import com.doyoumate.common.util.queryParams
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class PostRouter {
    @Bean
    fun postRoutes(handler: PostHandler): RouterFunction<ServerResponse> =
        router {
            "/post".nest {
                GET("", queryParams("content", "page", "size"), handler::searchPosts)
                GET("/my", handler::getMyPosts)
                GET("/liked", handler::getLikedPosts)
                GET("/popular", handler::getPopularPosts)
                GET("/{id}", handler::getPostById)
                POST("", contentType(MediaType.MULTIPART_FORM_DATA), handler::createPost)
                PUT("/{id}", contentType(MediaType.MULTIPART_FORM_DATA), handler::updatePostById)
                PATCH("/{id}/like", handler::likePostById)
                DELETE("/{id}", handler::deletePostById)
            }
        }
}
