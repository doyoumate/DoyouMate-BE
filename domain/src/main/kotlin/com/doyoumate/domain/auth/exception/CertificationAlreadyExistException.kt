package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class CertificationAlreadyExistException(
    override val message: String = "이미 보낸 인증번호가 있습니다. 잠시 후 다시 시도해주세요."
) : ServerException(code = 409, message)
