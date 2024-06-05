package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.global.util.sortBy
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.lt
import org.springframework.data.mongodb.core.query.regex
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class CustomLectureRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun search(
        year: Int?,
        grade: Int?,
        semester: Semester?,
        major: String?,
        name: String,
        credit: Int?,
        section: Section?,
        lastId: String?,
        size: Int
    ): Flux<Lecture> =
        Query()
            .apply {
                year?.let { addCriteria(Lecture::year isEqualTo it) }
                grade?.let { addCriteria(Lecture::grade isEqualTo it) }
                semester?.let { addCriteria(Lecture::semester isEqualTo it) }
                major?.let { addCriteria(Lecture::major isEqualTo it) }
                addCriteria(Lecture::name.regex(name, "i"))
                credit?.let { addCriteria(Lecture::credit isEqualTo it) }
                section?.let { addCriteria(Lecture::section isEqualTo it) }
                lastId?.let { addCriteria(Lecture::id lt it) }
                with(Lecture::id.sortBy(Sort.Direction.DESC))
                limit(size)
            }
            .let { mongoTemplate.find(it) }
}
