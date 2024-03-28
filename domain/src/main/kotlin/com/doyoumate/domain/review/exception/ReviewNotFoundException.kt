package com.doyoumate.domain.review.exception

import com.doyoumate.common.exception.ServerException

data class ReviewNotFoundException(
    override val message: String = "강의 평가를 찾을 수 없습니다."
) : ServerException(code = 404, message)
