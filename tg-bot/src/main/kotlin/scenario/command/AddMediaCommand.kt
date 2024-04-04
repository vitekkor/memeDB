package com.vitekkor.memeDB.scenario.command

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.TelegramBotRequest
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.channel.telegram.TelegramReactions
import com.justai.jaicf.channel.telegram.callback
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.generic.and
import com.justai.jaicf.model.activation.onlyFrom
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.memeDB.misc.MediaRepository
import com.vitekkor.memeDB.model.FileData
import com.vitekkor.memeDB.model.Media
import com.vitekkor.memeDB.model.TelegramAttachment
import com.vitekkor.memeDB.model.isNullOrEmpty
import com.vitekkor.memeDB.scenario.extension.attachments
import com.vitekkor.memeDB.service.addmediacommand.AddMediaCommandService
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class AddMediaCommand(
    private val addMediaCommandService: AddMediaCommandService,
    private val mediaRepository: MediaRepository,
) : BaseCommand() {
    private val log = KotlinLogging.logger {}
    override val name: String = "addmedia"

    override val description: String = "добавление мема в библиотеку"

    private val whiteList = setOf("361863012", "422760400", "495768224", "458955823", "848702672")

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
            event(TelegramEvent.ANIMATION)
            event(TelegramEvent.DOCUMENT)
            regex("/addmedia")
        }

        action(telegram) {
            val attachments = request.attachments()

            if (attachments.isNullOrEmpty()) {
                reactions.say("Отправь мне мем, который ты хочешь добавить. Пока что я умею принимать только картинки")
                return@action
            }
            val fileId = (attachments as TelegramAttachment).photoId.toString()

            context.client["fileId"] = fileId
            val description = request.message.caption?.removePrefix("/addmedia")
            if (!description.isNullOrBlank()) {
                request.saveMemeWithDescription(reactions, description, fileId)
                reactions.goBack()
                return@action
            }
            val replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                InlineKeyboardButton.CallbackData("Вручную", MANUAL),
                InlineKeyboardButton.CallbackData("Автоматически", AUTOMATIC)
            )
            reactions.say(text = "Добавить описание", replyMarkup = replyMarkup)
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.addManuallyDescription() {
        activators { regex(MANUAL).onlyFrom(telegram.callback) }

        action(telegram.callback and regex) {
            val messageId = request.message.messageId
            reactions.run {
                api.editMessageReplyMarkup(
                    chatId,
                    messageId,
                    replyMarkup = InlineKeyboardMarkup.create()
                )
            }
            reactions.say("Введите описание")
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.addAutoDescription() {
        activators { regex(AUTOMATIC).onlyFrom(telegram.callback) }

        action(telegram.callback and regex) {
            val messageId = request.message.messageId
            reactions.run {
                api.editMessageReplyMarkup(
                    chatId,
                    messageId,
                    replyMarkup = InlineKeyboardMarkup.create()
                )
            }
            reactions.say("Данная функциональность будет скоро доступна.")
            reactions.changeState("/")
            return@action

            reactions.say("Идет обработка файла...")

            val fileId = context.client["fileId"].toString()
            val fileBytes = reactions.telegram!!.api.downloadFileBytes(fileId)

            if (fileBytes == null) {
                reactions.say("Файл не найден")
                return@action
            }

            val chatId = reactions.telegram!!.chatId.id
            val fileData = FileData(chatId = chatId, messageId = messageId, fileId = fileId)

            addMediaCommandService.addFileBytes(fileData, fileBytes)
            reactions.say("Ваш мем скоро будет добавлен в базу")

            reactions.go("../../../")
        }
    }

    private fun StateBuilder<BotRequest, Reactions>.getDescription() {
        activators { regex("(?<descriptionText>.*)") }

        action(telegram) {
            val descriptionText = activator.regex?.group("descriptionText") ?: return@action

            context.client["descriptionText"] = descriptionText
            val fileId = context.client["fileId"].toString()
            request.saveMemeWithDescription(reactions, descriptionText, fileId)
            reactions.go("../../../")
        }
    }

    private fun TelegramBotRequest.saveMemeWithDescription(
        reactions: TelegramReactions,
        descriptionText: String,
        fileId: String,
    ): Boolean {
        val mediaData = Media(fileId, descriptionText)
        if (clientId in whiteList) {
            val fileBytes = reactions.telegram!!.api.downloadFileBytes(fileId)

            if (fileBytes == null) {
                reactions.say("Файл не найден")
                return false
            }
            try {
                addMediaCommandService.addMedia(mediaData, fileBytes)
            } catch (e: Exception) {
                log.error("Couldn't save image", e)
                reactions.say("Что-то пошло не так :(")
                return false
            }
            reactions.say("Ваш мем успешно добавлен в базу!")
        } else {
            mediaRepository.save(mediaData)
            reactions.say("Ваш мем скоро будет добавлен в базу")
        }
        return true
    }

    companion object {
        private const val MANUAL = "manual"
        private const val AUTOMATIC = "automatic"
    }
}
