package com.doyoumate.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
    @Value("\${api.uri}")
    private val uri: String
) {
    @Bean
    fun webClient(): WebClient =
        WebClient.builder()
            .baseUrl(uri)
            .codecs {
                it.defaultCodecs()
                    .maxInMemorySize(-1)
            }
            .build()
}
