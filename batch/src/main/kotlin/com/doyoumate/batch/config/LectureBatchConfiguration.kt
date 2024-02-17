package com.doyoumate.batch.config

import com.doyoumate.batch.tasklet.SynchronizeLecturesTasklet
import com.doyoumate.batch.util.job
import org.springframework.batch.core.Job
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class LectureBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val synchronizeLecturesTasklet: SynchronizeLecturesTasklet
) {
    @Bean
    fun synchronizeLecturesJob(): Job =
        job("synchronizeLecturesJob", jobRepository, transactionManager) {
            step("synchronizeLecturesStep") {
                chunk(1) {
                    tasklet(synchronizeLecturesTasklet)
                }
            }
        }
}
