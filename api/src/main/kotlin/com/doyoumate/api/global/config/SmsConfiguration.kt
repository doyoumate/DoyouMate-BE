package com.doyoumate.api.global.config

import net.nurigo.sdk.NurigoApp.initialize
import net.nurigo.sdk.message.service.DefaultMessageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SmsConfiguration(
    @Value("\${coolsms.apiKey}")
    private val apiKey: String,
    @Value("\${coolsms.secretKey}")
    private val secretKey: String,
) {
    @Bean
    fun smsService(): DefaultMessageService =
        initialize(apiKey, secretKey, "https://api.coolsms.co.kr")
}
