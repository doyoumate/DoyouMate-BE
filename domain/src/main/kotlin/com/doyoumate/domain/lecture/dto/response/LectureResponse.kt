package com.doyoumate.domain.lecture.dto.response

import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.Ratio

data class LectureResponse(
    val id: String,
    val professorId: String,
    val year: Int,
    val grade: Int,
    val semester: String,
    val major: String,
    val name: String,
    val professorName: String,
    val room: String,
    val date: String,
    val credit: Int,
    val section: String?,
    val type: String,
    val limitStudentCount: Int,
    val limitStudentGrade: List<Int>,
    val note: String,
    val ratio: Ratio
) {
    companion object {
        operator fun invoke(lecture: Lecture): LectureResponse =
            with(lecture) {
                LectureResponse(
                    id = id,
                    professorId = professorId,
                    year = year,
                    grade = grade,
                    semester = semester.semesterName,
                    major = major,
                    name = name,
                    professorName = professorName,
                    room = room,
                    date = date,
                    credit = credit,
                    section = section?.sectionName,
                    type = type.typeName,
                    limitStudentCount = limitStudentCount,
                    limitStudentGrade = limitStudentGrade,
                    note = note,
                    ratio = ratio
                )
            }
    }
}
