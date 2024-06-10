package com.doyoumate.batch.reader

import com.doyoumate.common.util.getLogger
import com.doyoumate.domain.student.adapter.StudentClient
import com.doyoumate.domain.student.model.Student
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.Year
import java.util.*

@Component
class WebClientStudentsReader(
    private val studentClient: StudentClient
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
                    .blockOptional()
            }
}
