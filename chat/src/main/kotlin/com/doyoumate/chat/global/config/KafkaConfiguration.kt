package com.doyoumate.chat.global.config

import com.doyoumate.domain.message.dto.response.MessageResponse
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import java.util.*

@Configuration
class ProducerConfiguration(
    private val properties: KafkaProperties
) {
    @Bean
    fun reactiveKafkaProducerTemplate(): ReactiveKafkaProducerTemplate<String, Any> =
        mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServers.first(),
        ).let {
            ReactiveKafkaProducerTemplate(
                SenderOptions.create<String, Any>(it)
                    .withKeySerializer(StringSerializer())
                    .withValueSerializer(JsonSerializer())
            )
        }
}

@Configuration
class ConsumerConfiguration(
    private val properties: KafkaProperties
) {
    @Bean("messageConsumer")
    fun messageConsumer(): ReactiveKafkaConsumerTemplate<String, MessageResponse> =
        ReactiveKafkaConsumerTemplate(createReceiverOptions(UUID.randomUUID().toString(), setOf("message")))

    private inline fun <reified T> createReceiverOptions(
        groupId: String,
        topics: Set<String>,
    ): ReceiverOptions<String, T> =
        mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServers.first(),
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ).let {
            ReceiverOptions.create<String, T>(it)
                .subscription(topics)
                .withKeyDeserializer(StringDeserializer())
                .withValueDeserializer(JsonDeserializer<T>(T::class.java, false))
        }
}
