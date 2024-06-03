package com.doyoumate.batch.reader

import com.doyoumate.common.util.collectHashSet
import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.student.adapter.StudentClient
import com.doyoumate.domain.student.model.Student
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Year
import java.util.*

@Component
class WebClientStudentsReader(
    private val studentClient: StudentClient,
    private val lectureClient: LectureClient
) : ItemReader<Optional<Student>> {
    private val logger = getLogger()
    private val years = (2018..Year.now().value).toList()
    private var number = 100000
    private var index = 0

    override fun read(): Optional<Student>? =
        years.getOrNull(index)
            ?.let { year ->
                if (number >= 102000) {
                    index += 1
                    number = 100000
                }

                "$year${number++}"
            }
            ?.let { studentNumber ->
                studentClient.getStudentByStudentNumber(studentNumber)
                    .doOnNext { logger.info { "Read: $it" } }
                    .flatMap { student ->
                        Mono.just(student)
                            .filter { it.status.run { contains("휴학") || contains("졸업") } }
                            .flatMap {
                                lectureClient.getAppliedLectureIdsByStudentNumber(studentNumber)
                                    .collectHashSet()
                            }
                            .zipWith(
                                lectureClient.getPreAppliedLectureIdsByStudentNumber(studentNumber)
                                    .collectHashSet()
                            )
                            .map { (appliedLectureIds, preAppliedLectureIds) ->
                                student.copy(
                                    appliedLectureIds = appliedLectureIds,
                                    preAppliedLectureIds = preAppliedLectureIds
                                )
                            }
                            .defaultIfEmpty(student)
                    }
                    .blockOptional()
            }
}
