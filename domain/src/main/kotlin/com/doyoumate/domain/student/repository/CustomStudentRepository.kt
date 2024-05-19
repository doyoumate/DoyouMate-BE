package com.doyoumate.domain.student.repository

import com.doyoumate.domain.global.util.set
import com.doyoumate.domain.global.util.setOnInsert
import com.doyoumate.domain.student.model.Student
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class CustomStudentRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun upsert(student: Student): Mono<Student> =
        with(student) {
            (Query(::number isEqualTo number)
                to
                Update()
                    .apply {
                        setOnInsert(::number to number)
                        set(::name to name)
                        set(::birthDate to birthDate)
                        set(::phoneNumber to phoneNumber)
                        set(::major to major)
                        set(::grade to grade)
                        set(::semester to semester)
                        set(::status to status)
                        set(::gpa to gpa)
                        set(::rank to rank)
                        setOnInsert(::role to role)
                        set(::appliedLectureIds to appliedLectureIds)
                        set(::preAppliedLectureIds to preAppliedLectureIds)
                        setOnInsert(::markedLecturesIds to emptySet())
                    }
                )
                .let { (query, update) ->
                    mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true))
                }
        }
}
