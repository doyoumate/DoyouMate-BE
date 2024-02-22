package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class PasswordNotMatchedException(
    override val message: String = "패스워드가 일치하지 않습니다."
) : ServerException(code = 403, message)
