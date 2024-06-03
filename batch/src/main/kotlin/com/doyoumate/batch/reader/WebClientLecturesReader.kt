package com.doyoumate.batch.reader

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Semester
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.Year

@Component
class WebClientLecturesReader(
    private val lectureClient: LectureClient
) : ItemReader<List<Lecture>> {
    private val logger = getLogger()
    private val semesters = enumValues<Semester>()
    private val years = (2020..Year.now().value).toList()
    private val pairs = years.flatMap { year ->
        semesters.map { semester ->
            year to semester
        }
    }
    private var index = 0

    override fun read(): List<Lecture>? =
        pairs.getOrNull(index++)
            ?.let { (year, semester) ->
                lectureClient.getLecturesByYearAndSemester(year, semester)
                    .doOnNext { logger.info { "Read: $it" } }
                    .collectList()
                    .block()
            }
}
