package com.doyoumate.batch.writer

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.student.model.Student
import com.doyoumate.domain.student.repository.CustomStudentRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import java.util.*

@Component
class MongoStudentsWriter(
    private val customStudentRepository: CustomStudentRepository
) : ItemWriter<Optional<Student>> {
    private val logger = getLogger()

    override fun write(chunk: Chunk<out Optional<Student>>) {
        chunk.items
            .filter { it.isPresent }
            .map {
                customStudentRepository.upsert(it.get())
                    .doOnNext { logger.info { "Write: $it" } }
                    .block()
            }
    }
}
