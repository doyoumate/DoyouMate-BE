package com.doyoumate.batch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import kotlin.system.exitProcess

@ComponentScan("com.doyoumate.batch", "com.doyoumate.domain", "com.doyoumate.common")
@SpringBootApplication
class BatchApplication

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(runApplication<BatchApplication>(*args)))
}
