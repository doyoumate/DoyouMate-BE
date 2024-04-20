package com.doyoumate.api.board.router

import com.doyoumate.api.board.handler.BoardHandler
import com.doyoumate.common.annotation.Router
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class BoardRouter {
    @Bean
    fun boardRoutes(handler: BoardHandler): RouterFunction<ServerResponse> =
        router {
            "/board".nest {
                GET("", handler::getBoards)
            }

            "/admin/board".nest {
                POST("", handler::createBoard)
                PUT("/{id}", handler::updateBoardById)
                DELETE("/{id}", handler::deleteBoardById)
            }
        }
}
