package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class AccountAlreadyExistException(
    override val message: String = "이미 가입된 학생 계정입니다."
) : ServerException(code = 409, message)
