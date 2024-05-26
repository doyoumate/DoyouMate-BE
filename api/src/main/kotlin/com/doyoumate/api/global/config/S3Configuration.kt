package com.doyoumate.api.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration

@Configuration
class S3Configuration(
    private val credentialsProvider: AwsCredentialsProvider,
    @Value("\${aws.s3.region}")
    private val region: String
) {
    @Bean
    fun amazonS3Client(): S3AsyncClient =
        S3AsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(
                S3Configuration.builder()
                    .checksumValidationEnabled(false)
                    .chunkedEncodingEnabled(true)
                    .build()
            )
            .build()
}
