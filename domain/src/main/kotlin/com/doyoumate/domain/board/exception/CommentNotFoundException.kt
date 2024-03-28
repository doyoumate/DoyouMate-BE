package com.doyoumate.domain.board.exception

import com.doyoumate.common.exception.ServerException

data class CommentNotFoundException(
    override val message: String = "댓글을 찾을 수 없습니다."
) : ServerException(code = 404, message)
