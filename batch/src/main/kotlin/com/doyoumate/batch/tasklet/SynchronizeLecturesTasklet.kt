package com.doyoumate.batch.tasklet

import com.doyoumate.batch.annotation.Tasklet
import com.doyoumate.batch.util.ItemTasklet
import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.lecture.repository.LectureRepository
import org.springframework.batch.item.Chunk
import reactor.core.publisher.Flux
import java.time.Year

@Tasklet
class SynchronizeLecturesTasklet(
    private val lectureRepository: LectureRepository,
    private val lectureClient: LectureClient
) : ItemTasklet<Flux<Lecture>> {
    private val semesters = enumValues<Semester>()
    private val years = (2020..Year.now().value).toList()
    private val pairs = years.flatMap { year ->
        semesters.map { semester ->
            year to semester
        }
    }
    private var idx = 0

    override fun read(): Flux<Lecture>? =
        pairs.getOrNull(idx++)
            ?.let { (year, semester) -> lectureClient.getLecturesByYearAndSemester(year, semester) }

    override fun write(chunk: Chunk<out Flux<Lecture>>) {
        lectureRepository.saveAll(chunk.items.first())
            .collectList()
            .block()
    }
}
