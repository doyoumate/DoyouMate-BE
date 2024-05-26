package com.doyoumate.api.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider

@Configuration
class AwsConfiguration(
    @Value("\${aws.credentials.accessKey}")
    private val accessKey: String,
    @Value("\${aws.credentials.secretKey}")
    private val secretKey: String
) {
    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider =
        AwsCredentialsProvider { AwsBasicCredentials.create(accessKey, secretKey) }
}
