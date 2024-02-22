package com.doyoumate.domain.lecture.dto.response

data class FilterResponse(
    val year: Set<Int>,
    val grade: Set<Int>,
    val semester: Set<String>,
    val major: Set<String>,
    val name: Set<String>,
    val credit: Set<Int>,
    val section: Set<String>
)
