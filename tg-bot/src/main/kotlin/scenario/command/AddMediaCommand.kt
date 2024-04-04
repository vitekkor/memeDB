package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions
import com.justai.jaicf.reactions.buttons
import com.justai.jaicf.reactions.toState
import com.vitekkor.memeDB.model.Media
import com.vitekkor.memeDB.model.TelegramAttachment
import com.vitekkor.memeDB.model.isNullOrEmpty
import com.vitekkor.memeDB.scenario.extension.attachments
import com.vitekkor.memeDB.service.addmediacommand.AddMediaCommandService
import org.springframework.stereotype.Component

@Component
class AddMediaCommand(private val addMediaCommandService: AddMediaCommandService) : BaseCommand() {
    override val name: String = "addMedia"

    override val description: String = "добавление мема в библиотеку"

    override val model: ScenarioModel = createModel {
        state(name) {
            commandAction()
            state("manuallyDescription") {
                addManuallyDescription()
                state("getDescription") {
                    getDescription()
                }
            }
            state("autoDescription") {
                addAutoDescription()
            }
        }
    }

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators {
            event(TelegramEvent.PHOTOS)
            regex("/addMedia")
        }

        action {
            val attachments = request.attachments()

            if (attachments.isNullOrEmpty()) {
                reactions.say("Вы не отправили медиа файл")
                return@action
            }

            reactions.say("Добавление фото")

            context.client["fileId"] = (attachments as TelegramAttachment).photoId
            reactions.say("Добавить описание")
            reactions.buttons("Вручную" toState  "manuallyDescription", "Автоматически" toState "autoDescription")
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.addManuallyDescription() {
        activators { regex("/manuallyDescription") }

        action {
            reactions.say("Введите описание")
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.addAutoDescription() {
        activators { regex("/autoDescription") }

        action {
            reactions.say("Функция будет добавлена позже")
            reactions.go("../manuallyDescription")
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.getDescription() {
        activators { regex("(?<descriptionText>.*)") }

        action {
            val descriptionText = activator.regex?.group("descriptionText") ?: return@action

            context.client["descriptionText"] = descriptionText

            val mediaData = Media(context.client["fileId"].toString(), context.client["descriptionText"].toString())
            addMediaCommandService.addMedia(mediaData)
            reactions.say("Ваш мем скоро будет добавлен в базу")
            reactions.go("../../../")
        }
    }
}
