package com.doyoumate.domain.student.repository

import com.doyoumate.domain.global.util.query
import com.doyoumate.domain.global.util.update
import com.doyoumate.domain.student.model.Student
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class CustomStudentRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun upsert(student: Student): Mono<Student> =
        with(student) {
            val query = query { "number" isEqualTo number }
            val update = update {
                "number" setOnInsert number
                "name" set name
                "birthDate" set birthDate
                "phoneNumber" set phoneNumber
                "major" set major
                "grade" set grade
                "semester" set semester
                "status" set status
                "gpa" set gpa
                "rank" set rank
                "role" setOnInsert role
                "appliedLectureIds" set appliedLectureIds
                "preAppliedLectureIds" set preAppliedLectureIds
                "markedLectureIds" setOnInsert hashSetOf<String>()
            }

            mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true))
        }
}
