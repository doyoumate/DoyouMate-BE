package com.doyoumate.domain.auth.exception

import com.doyoumate.common.exception.ServerException

data class PermissionDeniedException(
    override val message: String = "권한이 없습니다."
) : ServerException(code = 403, message)
