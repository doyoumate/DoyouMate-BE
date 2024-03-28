package com.doyoumate.domain.board.exception

import com.doyoumate.common.exception.ServerException

data class BoardNotFoundException(
    override val message: String = "게시판을 찾을 수 없습니다."
) : ServerException(code = 404, message)
