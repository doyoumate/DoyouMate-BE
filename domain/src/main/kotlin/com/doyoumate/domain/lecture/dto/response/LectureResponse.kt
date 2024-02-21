package com.doyoumate.domain.lecture.dto.response

import com.doyoumate.domain.lecture.model.Lecture

data class LectureResponse(
    val id: String,
    val courseNumber: String,
    val code: String,
    val year: Int,
    val grade: Int,
    val semester: String,
    val major: String,
    val name: String,
    val professor: String,
    val room: String,
    val date: String,
    val credit: Int,
    val section: String?
) {
    companion object {
        operator fun invoke(lecture: Lecture): LectureResponse =
            with(lecture) {
                LectureResponse(
                    id = id,
                    courseNumber = courseNumber,
                    code = code,
                    year = year,
                    grade = grade,
                    semester = semester.semesterName,
                    major = major,
                    name = name,
                    professor = professor,
                    room = room,
                    date = date,
                    credit = credit,
                    section = section?.sectionName
                )
            }
    }
}
