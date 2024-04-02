package com.vitekkor.memeDB.config

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotResponse
import com.justai.jaicf.channel.telegram.TelegramChannel
import com.justai.jaicf.context.BotContext
import com.justai.jaicf.context.RequestContext
import com.justai.jaicf.context.manager.BotContextManager
import com.justai.jaicf.logging.Slf4jConversationLogger
import com.vitekkor.memeDB.config.properties.BotConfigurationProperties
import com.vitekkor.memeDB.scenario.MainScenario
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MemeDBConfiguration {

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

    @Bean(name = ["ktorClient"])
    fun ktorClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(Logging) {
                level = LogLevel.INFO
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
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

    @Bean
    fun botApi(
        mainScenario: MainScenario,
        contextManager: BotContextManager,
    ) = BotEngine(
        scenario = mainScenario,
        defaultContextManager = contextManager,
        activators = arrayOf(RegexActivator),
        conversationLoggers = arrayOf(Slf4jConversationLogger())
    )

    @Bean
    fun teleramChannel(botApi: BotApi, botConfigurationProperties: BotConfigurationProperties, ktorClient: HttpClient) =
        TelegramChannel(
            botApi = botApi,
            telegramBotToken = botConfigurationProperties.telegramToken,
        ).apply {
            runBlocking { ktorClient.get<String>("https://api.telegram.org/bot${botConfigurationProperties.telegramToken}/deleteWebhook") }
            run()
        }

    @Bean
    fun contextManager(): BotContextManager = object : BotContextManager {
        override fun loadContext(request: BotRequest, requestContext: RequestContext): BotContext {
            return BotContext(request.clientId)
        }

        override fun saveContext(
            botContext: BotContext,
            request: BotRequest?,
            response: BotResponse?,
            requestContext: RequestContext
        ) {
            // skip saving
        }
    }
}