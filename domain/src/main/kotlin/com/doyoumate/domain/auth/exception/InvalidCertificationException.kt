package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class InvalidCertificationException(
    override val message: String = "유효하지 않은 전화번호 또는 인증번호입니다."
) : ServerException(code = 403, message)
