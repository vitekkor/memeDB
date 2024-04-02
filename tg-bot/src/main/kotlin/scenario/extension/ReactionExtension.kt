package com.vitekkor.memeDB.scenario.extension

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.channel.telegram.telegram
import com.vitekkor.memeDB.model.Attachment
import com.vitekkor.memeDB.model.TelegramAttachment

fun BotRequest.attachments(): Attachment? {
    if (telegram != null) {
        val photos = telegram!!.message.photo
            ?.sortedWith { a, b -> b.width * b.height - a.width * a.height }
            ?.first()?.fileId
            ?.let { listOf(it) }

        return TelegramAttachment(photos)
    }
    return null
}