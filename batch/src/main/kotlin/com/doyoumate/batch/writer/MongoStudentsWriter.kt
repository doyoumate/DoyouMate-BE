package com.doyoumate.batch.writer

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.board.model.Writer
import com.doyoumate.domain.board.repository.CommentRepository
import com.doyoumate.domain.board.repository.PostRepository
import com.doyoumate.domain.student.model.Student
import com.doyoumate.domain.student.repository.CustomStudentRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class MongoStudentsWriter(
    private val customStudentRepository: CustomStudentRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) : ItemWriter<Optional<Student>> {
    private val logger = getLogger()

    override fun write(chunk: Chunk<out Optional<Student>>) {
        chunk.items
            .filter { it.isPresent }
            .map { optional ->
                customStudentRepository.upsert(optional.get())
                    .doOnNext { logger.info { "Write: $it" } }
                    .flatMap { student ->
                        val writer = Writer(student)

                        postRepository.findAllByWriterId(student.id!!)
                            .map { it.copy(writer = writer) }
                            .let { postRepository.saveAll(it) }
                            .then(Mono.just(true))
                            .zipWith(
                                commentRepository.findAllByWriterId(student.id!!)
                                    .map { it.copy(writer = writer) }
                                    .let { commentRepository.saveAll(it) }
                                    .then(Mono.just(true))
                            )
                    }
                    .block()
            }
    }
}
