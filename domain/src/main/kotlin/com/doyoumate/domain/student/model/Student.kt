package com.doyoumate.domain.student.model

import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.student.model.enum.Role
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Document
data class Student(
    @Id
    val id: String? = null,
    val number: String,
    val name: String,
    val password: String? = null,
    val birthDate: LocalDate,
    val phoneNumber: String,
    val major: String,
    val grade: Int,
    val semester: Semester,
    val status: String,
    val gpa: Float?,
    val rank: Int?,
    val role: Role = Role.USER,
    val appliedLectureIds: HashSet<String> = hashSetOf(),
    val preAppliedLectureIds: HashSet<String> = hashSetOf(),
    val markedLecturesIds: HashSet<String> = hashSetOf()
) {
    companion object {
        operator fun invoke(profile: JsonNode, phoneNumber: String, gpa: Float, rank: Int): Student =
            with(profile) {
                Student(
                    number = getValue("STUNO"),
                    name = getValue("FNM"),
                    birthDate = LocalDate.parse(getValue("BIRYMD"), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    phoneNumber = phoneNumber,
                    major = getValue("FCLT_NM"),
                    grade = getValue("NOW_SHYS_CD"),
                    semester = Semester(getValue<Int>("NOW_SHTM_CD")),
                    status = "${getValue<String>("SCHREG_STAT_CHANGE_NM")}(${getValue<String>("SCHREG_CHANGE_DTL_NM")})",
                    gpa = gpa.takeIf { it != 0.0f },
                    rank = rank.takeIf { it != 0 }
                )
            }
    }
}
