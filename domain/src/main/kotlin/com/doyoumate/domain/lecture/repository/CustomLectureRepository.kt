package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.global.util.query
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class CustomLectureRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun searchLectures(
        year: Int?,
        grade: Int?,
        semester: Semester?,
        major: String?,
        name: String,
        credit: Int?,
        section: Section?
    ): Flux<Lecture> =
        query {
            "year" isEqualTo year
            "grade" isEqualTo grade
            "semester" isEqualTo semester
            "major" isEqualTo major
            "name" like name
            "credit" isEqualTo credit
            "section" isEqualTo section
        }.let {
            mongoTemplate.find(it, "lecture")
        }
}
