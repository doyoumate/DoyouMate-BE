package com.doyoumate.domain.student.repository

import com.doyoumate.domain.student.model.Student
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : ReactiveMongoRepository<Student, String>
