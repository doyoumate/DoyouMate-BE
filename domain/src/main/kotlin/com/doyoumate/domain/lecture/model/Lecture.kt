package com.doyoumate.domain.lecture.model

import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Lecture(
    @Id
    val id: String,
    val year: Int,
    val grade: Int,
    val semester: Semester,
    val major: String,
    val name: String,
    val professor: String,
    val room: String,
    val date: String,
    val credit: Int,
    val section: Section?,
    val plan: Plan
) {
    companion object {
        operator fun invoke(node: JsonNode, plan: Plan): Lecture =
            with(node) {
                Lecture(
                    id = getValue<String>("EDUCUR_CORS_NO") + getValue<String>("LECT_NO"),
                    year = getValue("OPEN_YY"),
                    grade = getValue("EDUCUR_CORS_SHYS_CD"),
                    semester = Semester(getValue<Int>("OPEN_SHTM_CD")),
                    major = getValue("ORGN4_NM"),
                    name = getValue("SBJT_NM"),
                    professor = getValue("FNM"),
                    room = getValue("LT_ROOM_NM"),
                    date = getValue("LTTM"),
                    credit = getValue("LCTPT"),
                    section = getValue<String>("CTNCCH_FLD_DIV_CD")
                        .run { if (isBlank()) null else Section(toInt()) },
                    plan = plan
                )
            }
    }
}
