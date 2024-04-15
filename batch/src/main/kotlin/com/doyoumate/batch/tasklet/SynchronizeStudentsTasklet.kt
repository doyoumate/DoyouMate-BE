package com.doyoumate.batch.tasklet

import com.doyoumate.batch.annotation.Tasklet
import com.doyoumate.batch.util.ItemTasklet
import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.student.adapter.StudentClient
import com.doyoumate.domain.student.model.Student
import com.doyoumate.domain.student.repository.CustomStudentRepository
import org.springframework.batch.item.Chunk
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Year

@Tasklet
class SynchronizeStudentsTasklet(
    private val customStudentRepository: CustomStudentRepository,
    private val studentClient: StudentClient,
    private val lectureClient: LectureClient
) : ItemTasklet<Mono<Student>> {
    private val years = (2018..Year.now().value).toList()
    private var number = 100000
    private var idx = 0

    override fun read(): Mono<Student>? =
        years.getOrNull(idx)
            ?.let { year ->
                if (number >= 102000) {
                    idx += 1
                    number = 100000
                }

                "$year${number++}"
            }
            ?.let {
                studentClient.getStudentById(it)
                    .flatMap { student ->
                        if (student.status.run { contains("휴학") || contains("졸업") }) {
                            Mono.just(student)
                        } else {
                            Mono.zip(
                                lectureClient.getAppliedLectureIdsByStudentId(it)
                                    .collectList(),
                                lectureClient.getPreAppliedLectureIdsByStudentId(it)
                                    .collectList()
                            ).map { (appliedLectureIds, preAppliedLectureIds) ->
                                student.copy(
                                    appliedLectureIds = appliedLectureIds.toHashSet(),
                                    preAppliedLectureIds = preAppliedLectureIds.toHashSet()
                                )
                            }
                        }
                    }
            }

    override fun write(chunk: Chunk<out Mono<Student>>) {
        Flux.concat(chunk.items)
            .flatMap { customStudentRepository.updateStudent(it) }
            .collectList()
            .block()
    }
}
