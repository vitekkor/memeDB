package com.vitekkor.memeDB.service.autocaption

import com.justai.jaicf.channel.invocationapi.InvocationEventRequest
import com.justai.jaicf.channel.telegram.TelegramChannel
import com.justai.jaicf.context.RequestContext
import com.vitekkor.memeDB.model.AutoCaptionDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

@Service
class AutoCaptionService(
    private val teleramChannel: TelegramChannel,
) {
    fun sendNotification(chatId: Long, messageId: Long, message: String, id: String) {
        teleramChannel.processInvocation(
            InvocationEventRequest(
                chatId.toString(),
                AUTO_CAPTION_EVENT,
                Json.encodeToString(AutoCaptionDto(message, messageId, id))
            ),
            RequestContext.DEFAULT
        )
    }

    companion object {
        const val AUTO_CAPTION_EVENT = "autoCaptionEvent"
    }
}
