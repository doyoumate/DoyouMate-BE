package com.doyoumate.api.professor.service

import com.doyoumate.domain.professor.dto.response.ProfessorResponse
import com.doyoumate.domain.professor.repository.ProfessorRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfessorService(
    private val professorRepository: ProfessorRepository
) {
    fun getProfessorById(id: String): Mono<ProfessorResponse> =
        professorRepository.findById(id)
            .map { ProfessorResponse(it) }
}
