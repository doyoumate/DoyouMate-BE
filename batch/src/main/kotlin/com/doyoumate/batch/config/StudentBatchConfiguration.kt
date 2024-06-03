package com.doyoumate.batch.config

import com.doyoumate.batch.reader.WebClientStudentsReader
import com.doyoumate.batch.writer.MongoStudentsWriter
import com.doyoumate.domain.student.model.Student
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.util.*

@Configuration
class StudentBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun updateStudentsJob(
        webClientStudentsReader: WebClientStudentsReader,
        mongoStudentsWriter: MongoStudentsWriter
    ): Job =
        JobBuilder("updateStudentsJob", jobRepository)
            .start(saveStudentsStep(webClientStudentsReader, mongoStudentsWriter))
            .build()

    @Bean
    fun saveStudentsStep(
        reader: WebClientStudentsReader,
        writer: MongoStudentsWriter
    ): Step =
        StepBuilder("saveStudentsStep", jobRepository)
            .chunk<Optional<Student>, Optional<Student>>(100, transactionManager)
            .reader(reader)
            .writer(writer)
            .build()
}
