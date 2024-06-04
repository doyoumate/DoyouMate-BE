package com.doyoumate.domain.professor.repository

import com.doyoumate.domain.professor.model.Professor
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessorRepository : ReactiveMongoRepository<Professor, String>
