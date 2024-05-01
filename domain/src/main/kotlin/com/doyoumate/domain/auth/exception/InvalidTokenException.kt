package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class InvalidTokenException(
    override val message: String = "유효하지 않은 토큰입니다."
) : ServerException(code = 403, message)
