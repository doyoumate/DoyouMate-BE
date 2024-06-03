package com.doyoumate.batch.writer

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.repository.LectureRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class MongoLecturesWriter(
    private val lectureRepository: LectureRepository
) : ItemWriter<List<Lecture>> {
    private val logger = getLogger()

    override fun write(chunk: Chunk<out List<Lecture>>) {
        lectureRepository.saveAll(chunk.items.first())
            .doOnNext { logger.info { "Write: $it" } }
            .blockLast()
    }
}
