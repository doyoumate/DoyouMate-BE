package com.doyoumate.domain.student.dto.response

import java.time.LocalDate

data class ChapelResponse(
    val date: String,
    val room: String,
    val seat: String,
    val attendances: List<Attendance>
) {
    companion object {
        val COLUMNS = listOf("가", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타")
        val DAYS = listOf("일", "월", "화", "수", "목", "금", "토")
    }
}

data class Attendance(
    val date: LocalDate,
    val isAttended: Boolean,
    val isOnline: Boolean
)
