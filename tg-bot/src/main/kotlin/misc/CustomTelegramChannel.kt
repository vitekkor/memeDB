package com.vitekkor.memeDB.misc

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.animation
import com.github.kotlintelegrambot.dispatcher.audio
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.document
import com.github.kotlintelegrambot.dispatcher.inlineQuery
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.sticker
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.dispatcher.video
import com.github.kotlintelegrambot.dispatcher.videoNote
import com.github.kotlintelegrambot.dispatcher.voice
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.github.kotlintelegrambot.updater.Updater
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.api.EventBotRequest
import com.justai.jaicf.channel.http.HttpBotRequest
import com.justai.jaicf.channel.http.HttpBotResponse
import com.justai.jaicf.channel.invocationapi.InvocableBotChannel
import com.justai.jaicf.channel.invocationapi.InvocationRequest
import com.justai.jaicf.channel.invocationapi.getRequestTemplateFromResources
import com.justai.jaicf.channel.jaicp.JaicpCompatibleAsyncBotChannel
import com.justai.jaicf.channel.jaicp.JaicpCompatibleAsyncChannelFactory
import com.justai.jaicf.channel.jaicp.JaicpLiveChatProvider
import com.justai.jaicf.channel.telegram.TelegramAnimationRequest
import com.justai.jaicf.channel.telegram.TelegramAudioRequest
import com.justai.jaicf.channel.telegram.TelegramBotRequest
import com.justai.jaicf.channel.telegram.TelegramDocumentRequest
import com.justai.jaicf.channel.telegram.TelegramInvocationRequest
import com.justai.jaicf.channel.telegram.TelegramPhotosRequest
import com.justai.jaicf.channel.telegram.TelegramQueryRequest
import com.justai.jaicf.channel.telegram.TelegramReactions
import com.justai.jaicf.channel.telegram.TelegramStickerRequest
import com.justai.jaicf.channel.telegram.TelegramTextRequest
import com.justai.jaicf.channel.telegram.TelegramVideoNoteRequest
import com.justai.jaicf.channel.telegram.TelegramVideoRequest
import com.justai.jaicf.channel.telegram.TelegramVoiceRequest
import com.justai.jaicf.context.RequestContext
import com.justai.jaicf.helpers.http.withTrailingSlash
import com.justai.jaicf.helpers.kotlin.PropertyWithBackingField
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CustomTelegramChannel(
    override val botApi: BotApi,
    private val telegramBotToken: String,
    private val telegramApiUrl: String = "https://api.telegram.org/",
    private val telegramLogLevel: LogLevel = LogLevel.None,
    private val requestExecutor: Executor = Executors.newFixedThreadPool(10)
) : JaicpCompatibleAsyncBotChannel, InvocableBotChannel {

    private val gson = GsonFactory.createForApiClient()
    private var liveChatProvider: JaicpLiveChatProvider? = null
    private lateinit var botUpdater: Updater

    private val bot = bot {
        apiUrl = telegramApiUrl.withTrailingSlash()
        token = telegramBotToken
        botUpdater = updater
        logLevel = telegramLogLevel

        botUpdater.startCheckingUpdates()

        dispatch {
            fun process(request: TelegramBotRequest) {
                requestExecutor.execute {
                    botApi.process(
                        request,
                        TelegramReactions(bot, request, liveChatProvider),
                        RequestContext.fromHttp(request.update.httpBotRequest)
                    )
                }
            }

            text {
                process(TelegramTextRequest(update, message))
            }

            callbackQuery {
                val message = callbackQuery.message ?: return@callbackQuery
                process(TelegramQueryRequest(update, message, callbackQuery.data))
            }

            audio {
                process(TelegramAudioRequest(update, message, media))
            }

            document {
                process(TelegramDocumentRequest(update, message, media))
            }

            animation {
                process(TelegramAnimationRequest(update, message, media))
            }

            photos {
                process(TelegramPhotosRequest(update, message, media))
            }

            sticker {
                process(TelegramStickerRequest(update, message, media))
            }

            video {
                process(TelegramVideoRequest(update, message, media))
            }

            videoNote {
                process(TelegramVideoNoteRequest(update, message, media))
            }

            voice {
                process(TelegramVoiceRequest(update, message, media))
            }
            inlineQuery {
                process(
                    TelegramInlineQueryRequest(
                        update,
                        inlineQuery.id,
                        inlineQuery.query,
                        inlineQuery.from.id.toString()
                    )
                )
            }
        }
    }

    override fun process(request: HttpBotRequest): HttpBotResponse {
        val update = gson.fromJson(request.receiveText(), Update::class.java)
        update.httpBotRequest = request
        bot.processUpdate(update)
        return HttpBotResponse.accepted()
    }

    private fun generateRequestFromTemplate(request: InvocationRequest) =
        getRequestTemplateFromResources(request, REQUEST_TEMPLATE_PATH)
            .replace("\"{{ timestamp }}\"", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString())
            .replace("{{ messageId }}", UUID.randomUUID().toString())


    override fun processInvocation(request: InvocationRequest, requestContext: RequestContext) {
        val generatedRequest = generateRequestFromTemplate(request)
        val update = gson.fromJson(generatedRequest, Update::class.java) ?: return
        val message = update.message ?: return
        val telegramRequest = TelegramInvocationRequest.create(request, update, message) ?: return
        botApi.process(telegramRequest, TelegramReactions(bot, telegramRequest, liveChatProvider), requestContext)
    }

    fun run() {
        botUpdater.stopCheckingUpdates()
        bot.startPolling()
    }

    companion object : JaicpCompatibleAsyncChannelFactory {
        override val channelType = "telegram"
        override fun create(
            botApi: BotApi,
            apiUrl: String,
            liveChatProvider: JaicpLiveChatProvider,
        ) = CustomTelegramChannel(botApi, telegramApiUrl = apiUrl, telegramBotToken = "").apply {
            this.liveChatProvider = liveChatProvider
        }

        private const val REQUEST_TEMPLATE_PATH = "/TelegramRequestTemplate.json"
    }

    class Jaicp(
        private val executor: Executor,
        private val logLevel: LogLevel
    ) : JaicpCompatibleAsyncChannelFactory {

        override fun create(
            botApi: BotApi,
            apiUrl: String,
            liveChatProvider: JaicpLiveChatProvider
        ): JaicpCompatibleAsyncBotChannel = CustomTelegramChannel(botApi, "", apiUrl, logLevel, executor).apply {
            this.liveChatProvider = liveChatProvider
        }

        override val channelType: String = "telegram"
    }
}

internal var Update.httpBotRequest: HttpBotRequest by PropertyWithBackingField {
    HttpBotRequest("".byteInputStream())
}

data class TelegramInlineQueryRequest(
    override val update: Update,
    val id: String,
    val query: String,
    override val clientId: String,
    override val message: Message = EMPTY_MESSAGE
) : TelegramBotRequest, EventBotRequest(clientId, CustomTelegramEvent.INLINE_QUERY_EVENT)

object CustomTelegramEvent {
    const val INLINE_QUERY_EVENT = "inlineQueryEvent"
}

private val EMPTY_MESSAGE = Message(messageId = -1, chat = Chat(1, ""), date = -1)
