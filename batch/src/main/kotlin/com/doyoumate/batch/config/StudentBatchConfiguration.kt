package com.doyoumate.batch.config

import com.doyoumate.batch.tasklet.SynchronizeStudentsTasklet
import com.doyoumate.batch.util.job
import org.springframework.batch.core.Job
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class StudentBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val synchronizeStudentsTasklet: SynchronizeStudentsTasklet
) {
    @Bean
    fun synchronizeStudentsJob(): Job =
        job("synchronizeStudentsJob", jobRepository, transactionManager) {
            step("synchronizeStudentsStep") {
                chunk(100) {
                    tasklet(synchronizeStudentsTasklet)
                }
            }
        }
}
