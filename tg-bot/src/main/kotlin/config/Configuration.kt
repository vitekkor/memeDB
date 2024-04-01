package com.vitekkor.memeDB.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.LogLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean(name = ["telegramClient"])
    fun telegramClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(Logging) {
                level = LogLevel.INFO
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            install(HttpTimeout)
            engine {
                requestTimeout = 60_000
                endpoint {
                    connectAttempts = 2
                    connectTimeout = 2_500
                }
            }
        }
    }
}