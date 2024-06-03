package com.doyoumate.batch.config

import com.doyoumate.batch.reader.WebClientLecturesReader
import com.doyoumate.batch.writer.MongoLecturesWriter
import com.doyoumate.domain.lecture.model.Lecture
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class LectureBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,

    ) {
    @Bean
    fun updateLecturesJob(
        webClientLecturesReader: WebClientLecturesReader,
        mongoLecturesWriter: MongoLecturesWriter
    ): Job =
        JobBuilder("updateLecturesJob", jobRepository)
            .start(saveLecturesStep(webClientLecturesReader, mongoLecturesWriter))
            .build()

    @Bean
    fun saveLecturesStep(
        reader: WebClientLecturesReader,
        writer: MongoLecturesWriter
    ): Step =
        StepBuilder("saveLecturesStep", jobRepository)
            .chunk<List<Lecture>, List<Lecture>>(1, transactionManager)
            .reader(reader)
            .writer(writer)
            .build()
}
