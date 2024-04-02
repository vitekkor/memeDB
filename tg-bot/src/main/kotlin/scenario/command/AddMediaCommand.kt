package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.memeDB.scenario.extension.attachments
import com.vitekkor.memeDB.service.addmediacommand.AddMediaCommandService
import org.springframework.stereotype.Component

@Component
class AddMediaCommand(private val addMediaCommandService: AddMediaCommandService) : BaseCommand() {
    override val name: String = "addMedia"

    override val description: String = "добавление мема в библиотеку"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators {
            event(TelegramEvent.PHOTOS)
            regex("/addMedia")
        }

        action {
            val attachments = request.attachments()
            reactions.say("Добавление фото")
            reactions.go("addDescription")
        }
    }
}