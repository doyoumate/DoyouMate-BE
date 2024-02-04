package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.lecture.model.Lecture
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface LectureRepository : ReactiveMongoRepository<Lecture, String>
