package com.doyoumate.batch.writer

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.professor.model.Professor
import com.doyoumate.domain.professor.repository.ProfessorRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class MongoProfessorsWriter(
    private val professorRepository: ProfessorRepository
) : ItemWriter<Professor> {
    private val logger = getLogger()

    override fun write(chunk: Chunk<out Professor>) {
        professorRepository.saveAll(chunk.items)
            .doOnNext { logger.info { "Write: $it" } }
            .blockLast()
    }
}
