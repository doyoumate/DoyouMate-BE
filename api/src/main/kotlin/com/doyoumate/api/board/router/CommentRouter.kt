package com.doyoumate.api.board.router

import com.doyoumate.api.board.handler.CommentHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class CommentRouter {
    @Bean
    fun commentRoutes(handler: CommentHandler): RouterFunction<ServerResponse> =
        router {
            "/comment".nest {
                GET("/postId/{postId}", handler::getCommentsByPostId)
                GET("/writerId/{writerId}", handler::getCommentsByWriterId)
                POST("", handler::createComment)
                PUT("/{id}", handler::updateCommentById)
                PATCH("/{id}/like", handler::likeCommentById)
                DELETE("/{id}", handler::deleteCommentById)
            }
        }
}
