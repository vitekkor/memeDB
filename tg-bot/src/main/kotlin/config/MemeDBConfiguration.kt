package com.vitekkor.memeDB.config

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.context.manager.mongo.MongoBotContextManager
import com.justai.jaicf.logging.Slf4jConversationLogger
import com.vitekkor.memeDB.config.properties.BotConfigurationProperties
import com.vitekkor.memeDB.misc.CustomTelegramChannel
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
import org.springframework.data.mongodb.MongoDatabaseFactory

@Configuration
class MemeDBConfiguration(
    private val mongoDatabaseFactory: MongoDatabaseFactory,
    private val botConfigurationProperties: BotConfigurationProperties
) {

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
    ) = BotEngine(
        scenario = mainScenario,
        defaultContextManager = mongoDatabaseFactory.createContextManager(
            botConfigurationProperties.mongoCollection
        ),
        activators = arrayOf(RegexActivator),
        conversationLoggers = arrayOf(Slf4jConversationLogger())
    )

    @Bean
    fun teleramChannel(botApi: BotApi, ktorClient: HttpClient) =
        CustomTelegramChannel(
            botApi = botApi,
            telegramBotToken = botConfigurationProperties.telegramToken,
        ).apply {
            runBlocking { ktorClient.get<String>("https://api.telegram.org/bot${botConfigurationProperties.telegramToken}/deleteWebhook") }
            run()
        }

    companion object {
        private fun MongoDatabaseFactory.createContextManager(collection: String) =
            MongoBotContextManager(mongoDatabase.getCollection(collection))
    }
}