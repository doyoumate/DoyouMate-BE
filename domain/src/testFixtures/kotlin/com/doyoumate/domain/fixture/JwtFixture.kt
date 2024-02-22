package com.doyoumate.domain.fixture

import com.github.jwt.config.JwtConfiguration
import com.github.jwt.security.JwtAuthentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val SECRET_KEY = (1..100).map { ('a'..'z').random() }.joinToString("")
const val ACCESS_TOKEN_EXPIRE = 5L
const val REFRESH_TOKEN_EXPIRE = 10L
val AUTHORITIES = setOf(SimpleGrantedAuthority("USER"))
val jwtProvider = JwtConfiguration()
    .jwtProvider(SECRET_KEY, ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE)
val AUTHENTICATION = createJwtAuthentication()
val ACCESS_TOKEN = jwtProvider.createAccessToken(AUTHENTICATION)
val REFRESH_TOKEN = jwtProvider.createRefreshToken(AUTHENTICATION)

fun createJwtAuthentication(
    id: String = ID,
    authorities: Set<GrantedAuthority> = AUTHORITIES
): JwtAuthentication =
    JwtAuthentication(
        id = id,
        authorities = authorities
    )
