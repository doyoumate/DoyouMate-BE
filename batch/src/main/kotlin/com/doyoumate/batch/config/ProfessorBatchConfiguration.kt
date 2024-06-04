package com.doyoumate.batch.config

import com.doyoumate.batch.reader.WebClientProfessorsReader
import com.doyoumate.batch.writer.MongoProfessorsWriter
import com.doyoumate.domain.professor.model.Professor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ProfessorBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun updateProfessorsJob(
        webClientProfessorsReader: WebClientProfessorsReader,
        mongoProfessorsWriter: MongoProfessorsWriter
    ): Job =
        JobBuilder("updateProfessorsJob", jobRepository)
            .start(saveProfessorStep(webClientProfessorsReader, mongoProfessorsWriter))
            .build()

    @Bean
    fun saveProfessorStep(
        reader: WebClientProfessorsReader,
        writer: MongoProfessorsWriter
    ): Step =
        StepBuilder("saveProfessorsStep", jobRepository)
            .chunk<Professor, Professor>(100, transactionManager)
            .reader(reader)
            .writer(writer)
            .build()
}
