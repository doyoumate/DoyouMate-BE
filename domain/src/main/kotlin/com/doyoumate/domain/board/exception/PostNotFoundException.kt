package com.doyoumate.domain.board.exception

import com.doyoumate.common.exception.ServerException

data class PostNotFoundException(
    override val message: String = "게시물을 찾을 수 없습니다."
) : ServerException(code = 404, message)
