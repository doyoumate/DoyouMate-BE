package com.doyoumate.api.board.router

import com.doyoumate.api.board.handler.PostHandler
import com.doyoumate.common.annotation.Router
import com.doyoumate.common.util.queryParams
import org.springframework.context.annotation.Bean
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
                GET("/studentId/{studentId}", handler::getPostsByStudentId)
                GET("/popular", handler::getPopularPosts)
                POST("", handler::createPost)
                PUT("/{id}", handler::updatePostById)
                PATCH("/{id}/like", handler::likePostById)
                DELETE("/{id}", handler::deletePostById)
            }
        }
}
