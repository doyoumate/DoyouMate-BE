package com.doyoumate.batch.util

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.builder.SimpleJobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.SimpleStepBuilder
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.transaction.PlatformTransactionManager

fun job(
    name: String,
    repository: JobRepository,
    transactionManager: PlatformTransactionManager,
    init: JobDsl.() -> Unit
): Job =
    JobDsl(name, repository, transactionManager)
        .apply(init)
        .registerSteps()
        .build()

class JobDsl(
    private val name: String,
    private val repository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {
    private val jobBuilder = JobBuilder(name, repository)
    private val steps = mutableListOf<Step>()

    fun <T> step(name: String, init: StepDsl<T>.() -> SimpleStepBuilder<T, T>) {
        jobBuilder.start(
            StepDsl<T>(name, repository, transactionManager)
                .init()
                .build()
        )
        steps.add(
            StepDsl<T>(name, repository, transactionManager)
                .init()
                .build()
        )
    }

    fun registerSteps(): SimpleJobBuilder {
        lateinit var simpleJobBuilder: SimpleJobBuilder

        steps.mapIndexed { index, step ->
            if (index == 0) {
                simpleJobBuilder = jobBuilder.start(step)
            } else {
                simpleJobBuilder.next(step)
            }
        }

        return simpleJobBuilder
    }
}

class StepDsl<T>(
    private val name: String,
    private val repository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {
    private val stepBuilder = StepBuilder(name, repository)

    fun chunk(count: Int, init: SimpleStepBuilder<T, T>.() -> SimpleStepBuilder<T, T>): SimpleStepBuilder<T, T> =
        stepBuilder.chunk<T, T>(count, transactionManager)
            .init()

    fun SimpleStepBuilder<T, T>.tasklet(itemTasklet: ItemTasklet<T>): SimpleStepBuilder<T, T> =
        reader(itemTasklet::read)
            .writer(itemTasklet::write)
}
