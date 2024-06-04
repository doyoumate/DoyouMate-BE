package com.doyoumate.batch.reader

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.professor.adapter.ProfessorClient
import com.doyoumate.domain.professor.model.Professor
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component

@Component
class WebClientProfessorsReader(
    private val professorClient: ProfessorClient,
    private val lectureRepository: LectureRepository
) : ItemReader<Professor> {
    private val logger = getLogger()
    private val professorIds by lazy {
        lectureRepository.getProfessorIds()
            .collectList()
            .block()!!
    }
    private var index = 0

    override fun read(): Professor? =
        professorIds.getOrNull(index++)
            ?.let { id ->
                professorClient.getProfessorById(id)
                    .doOnNext { logger.info { "Read: $it" } }
                    .block()!!
            }
}
