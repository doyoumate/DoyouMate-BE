package com.doyoumate.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun webClient(): WebClient =
        WebClient.builder()
            .codecs {
                it.defaultCodecs()
                    .maxInMemorySize(-1)
            }
            .build()
}
