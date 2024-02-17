package com.doyoumate.batch.util

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import java.util.*

interface ItemTasklet<T> : ItemReader<T>, ItemWriter<T>

fun JobLauncher.run(job: Job): JobExecution = run(job, createJobParameters())

private fun createJobParameters(): JobParameters =
    hashMapOf<String, JobParameter<*>>()
        .apply {
            put("time", JobParameter(Date().time, Long::class.java))
        }
        .let { JobParameters(it) }
