package com.doyoumate.domain.lecture.model.enum

import java.time.LocalDate

enum class Semester(
    val semesterName: String,
    val id: Int,
) {
    FIRST("1학기 정규", 10),
    SECOND("2학기 정규", 20),
    FIRST_SEASONAL("1학기 계절", 15),
    SECOND_SEASONAL("2학기 계절", 25);
}
