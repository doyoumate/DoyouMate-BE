package com.doyoumate.domain.fixture

import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester

const val COURSE_NUMBER = "2023000000"
const val YEAR = 2024
const val PROFESSOR = "얼그레이"
const val ROOM = "다니엘관"
const val DATE = "토2~4"
const val CREDIT = 3
val SECTION = Section.BASIC_EDUCATION

fun createLectureResponse(
    lecture: Lecture = createLecture()
): LectureResponse =
    LectureResponse(lecture = lecture)

fun createLecture(
    id: String = ID,
    courseNumber: String = COURSE_NUMBER,
    code: String = CODE,
    year: Int = YEAR,
    grade: Int = GRADE,
    semester: Semester = SEMESTER,
    major: String = MAJOR,
    name: String = NAME,
    professor: String = PROFESSOR,
    room: String = ROOM,
    date: String = DATE,
    credit: Int = CREDIT,
    section: Section = SECTION
): Lecture =
    Lecture(
        id = id,
        courseNumber = courseNumber,
        code = code,
        year = year,
        grade = grade,
        semester = semester,
        major = major,
        name = name,
        professor = professor,
        room = room,
        date = date,
        credit = credit,
        section = section
    )
