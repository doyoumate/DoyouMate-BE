package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.lecture.model.Lecture
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LectureRepository : ReactiveMongoRepository<Lecture, String>
