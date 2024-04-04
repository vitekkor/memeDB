package com.vitekkor.memeDB

import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import service.kafka.CustomDeserializer
import service.kafka.dto.CaptionCallbackDto

@EnableConfigurationProperties
@ConfigurationPropertiesScan("com.vitekkor.memeDB.config")
@EnableKafka
@SpringBootApplication
class MemeDBBotApplication

fun main(args: Array<String>) {
    runApplication<MemeDBBotApplication>(*args)
}

@Value("\${spring.kafka.bootstrap-servers}")
private lateinit var bootstrapServers: String

@Bean
fun consumerFactory(): ConsumerFactory<String, CaptionCallbackDto> {
    val configProps: MutableMap<String, Any> = HashMap()
    configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = CustomDeserializer::class.java
    configProps[ConsumerConfig.GROUP_ID_CONFIG] = "consumer-group"

    return DefaultKafkaConsumerFactory(configProps)
}

@Bean
fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CaptionCallbackDto> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, CaptionCallbackDto>()
    factory.consumerFactory = consumerFactory()
    return factory
}
