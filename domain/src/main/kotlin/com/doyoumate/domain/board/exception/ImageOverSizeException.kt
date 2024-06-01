package com.doyoumate.domain.board.exception

import com.doyoumate.common.exception.ServerException

data class ImageOverSizeException(
    override val message: String = "이미지 크기는 1MB 이하여야 합니다."
) : ServerException(code = 403, message)
