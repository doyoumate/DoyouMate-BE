package com.doyoumate.domain.lecture.exception

import com.doyoumate.common.exception.ServerException

data class LectureNotFoundException(
    override val message: String = "강의를 찾을 수 없습니다."
) : ServerException(code = 404, message)
