package com.doyoumate.api.board.router

import com.doyoumate.api.board.handler.PostHandler
import com.doyoumate.common.annotation.Router
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
                GET("/{id}", handler::getPostById)
                GET("/board/{boardId}", handler::getPostsByBoardId)
                POST("", handler::createPost)
                PUT("/{id}", handler::updatePostById)
                PATCH("/{id}/like", handler::likePostById)
                DELETE("/{id}", handler::deletePostById)
            }
        }
}
