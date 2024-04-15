package com.doyoumate.api.global.config

import com.doyoumate.domain.student.model.enum.Role
import com.github.jwt.core.JwtProvider
import com.github.jwt.security.JwtAuthentication
import com.github.jwt.security.ReactiveJwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

@EnableWebFluxSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
        jwtFilter: ReactiveJwtFilter
    ): SecurityWebFilterChain =
        with(http) {
            csrf { it.disable() }
            formLogin { it.disable() }
            httpBasic { it.disable() }
            logout { it.disable() }
            requestCache { it.disable() }
            securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            exceptionHandling {
                it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            authorizeExchange {
                it.pathMatchers("/auth/**")
                    .permitAll()
                    .pathMatchers("/admin/**")
                    .hasAuthority(Role.ADMIN.name)
                    .anyExchange()
                    .authenticated()
            }
            addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            build()
        }

    @Bean
    fun jwtFilter(jwtProvider: JwtProvider): ReactiveJwtFilter =
        ReactiveJwtFilter(jwtProvider)

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()
}

fun ServerRequest.getAuthentication(): Mono<JwtAuthentication> =
    principal()
        .cast<JwtAuthentication>()
