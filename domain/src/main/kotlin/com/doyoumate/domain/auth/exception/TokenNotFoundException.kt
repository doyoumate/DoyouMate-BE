package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class TokenNotFoundException(
    override val message: String = "토큰을 찾을 수 없습니다."
) : ServerException(code = 404, message)
