package com.doyoumate.domain.lecture.dto.response

import com.doyoumate.domain.lecture.model.Filter

data class FilterResponse(
    val year: Set<Int>,
    val grade: Set<Int>,
    val semester: Set<String>,
    val major: Set<String>,
    val credit: Set<Int>,
    val section: Set<String>
) {
    companion object {
        operator fun invoke(filter: Filter): FilterResponse =
            with(filter) {
                FilterResponse(
                    year = year,
                    grade = grade,
                    semester = semester.map { it.semesterName }
                        .toSet(),
                    major = major,
                    credit = credit,
                    section = section.map { it.sectionName }
                        .toSet()
                )
            }
    }
}
