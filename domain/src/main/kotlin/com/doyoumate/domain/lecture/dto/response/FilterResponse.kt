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
                    year = year.toSortedSet(compareByDescending { it }),
                    grade = grade.toSortedSet(),
                    semester = semester.map { it.semesterName }
                        .toSortedSet(),
                    major = major.toSortedSet(),
                    credit = credit.toSortedSet(),
                    section = section.map { it.sectionName }
                        .toSortedSet()
                )
            }
    }
}
