package com.doyoumate.api.config

import com.doyoumate.api.global.config.SecurityConfiguration
import com.doyoumate.domain.fixture.jwtProvider
import com.github.jwt.security.ReactiveJwtFilter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class SecurityTestConfiguration {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        SecurityConfiguration()
            .filterChain(http, jwtFilter())

    @Bean
    fun jwtFilter(): ReactiveJwtFilter =
        ReactiveJwtFilter(jwtProvider)
}
