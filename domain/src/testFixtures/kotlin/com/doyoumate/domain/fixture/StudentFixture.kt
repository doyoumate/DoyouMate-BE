package com.doyoumate.domain.fixture

import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.student.model.Student
import java.time.LocalDate

const val NAME = "얼그레이"
const val ROW_PASSWORD = "root"
val PASSWORD = passwordEncoder.encode(ROW_PASSWORD)!!
val BIRTH_DATE = LocalDate.of(2002, 1, 1)!!
const val PHONE_NUMBER = "01012345678"
const val MAJOR = "컴퓨터공학부"
const val GRADE = 2
val SEMESTER = Semester.FIRST
const val STATUS = "재학"
const val GPA = 4.3F
val LECTURE_IDS = hashSetOf("1", "2", "2")

fun createStudent(
    id: String = ID,
    name: String = NAME,
    password: String? = PASSWORD,
    birthDate: LocalDate = BIRTH_DATE,
    phoneNumber: String? = PHONE_NUMBER,
    major: String = MAJOR,
    grade: Int = GRADE,
    semester: Semester = SEMESTER,
    status: String = STATUS,
    gpa: Float? = GPA,
    lectureIds: HashSet<String> = LECTURE_IDS.toHashSet()
): Student =
    Student(
        id = id,
        name = name,
        password = password,
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        major = major,
        grade = grade,
        semester = semester,
        status = status,
        gpa = gpa,
        lectureIds = lectureIds
    )
