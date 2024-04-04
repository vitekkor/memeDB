package com.vitekkor.memeDB.scenario

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.invocationapi.invocationRequest
import com.justai.jaicf.channel.telegram.callback
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.generic.and
import com.justai.jaicf.model.activation.onlyFrom
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.vitekkor.memeDB.model.AutoCaptionDto
import com.vitekkor.memeDB.service.autocaption.AutoCaptionService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class AutoCaptionScenario : Scenario {
    override val model: ScenarioModel = createModel {
        state(AutoCaptionService.AUTO_CAPTION_EVENT) {
            activators { event(AutoCaptionService.AUTO_CAPTION_EVENT) }
            action(telegram) {
                val input = checkNotNull(request.invocationRequest).requestData
                val caption = Json.decodeFromString<AutoCaptionDto>(input)
                val replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                    InlineKeyboardButton.CallbackData("Да", "$ACCEPT_AUTO_CAPTION ${caption.id}"),
                    InlineKeyboardButton.CallbackData("Нет", "$DECLINE_AUTO_CAPTION ${caption.id}")
                )
                reactions.say(text = caption.text, replyToMessageId = caption.replyMessageId)
                reactions.say("Подходящее описание для вашего мема?", replyMarkup = replyMarkup)
            }
        }
        state(ACCEPT_AUTO_CAPTION) {
            activators { regex("$ACCEPT_AUTO_CAPTION .*").onlyFrom(telegram.callback) }
            action(telegram.callback and com.justai.jaicf.activator.regex.regex) {
                val messageId = request.message.messageId
                val data = request.input.removePrefix("$ACCEPT_AUTO_CAPTION ")
                reactions.telegram?.run {
                    api.editMessageReplyMarkup(
                        chatId,
                        messageId,
                        replyMarkup = InlineKeyboardMarkup.create()
                    )
                }
                // TODO get from mongo by id
                reactions.say("Ваш мем скоро будет добавлен в базу")
            }
        }

        state(DECLINE_AUTO_CAPTION) {
            activators { regex("$DECLINE_AUTO_CAPTION .*").onlyFrom(telegram.callback) }
            action(telegram.callback and com.justai.jaicf.activator.regex.regex) {
                val messageId = request.message.messageId
                val data = request.input.removePrefix("$ACCEPT_AUTO_CAPTION ")
                reactions.telegram?.run {
                    api.editMessageReplyMarkup(
                        chatId,
                        messageId,
                        replyMarkup = InlineKeyboardMarkup.create()
                    )
                }
                // TODO get from mongo by id and save file ide into context
                reactions.go("/addMedia/manuallyDescription")
            }
        }
    }

    companion object {
        const val ACCEPT_AUTO_CAPTION = "acceptAutoCaption"
        const val DECLINE_AUTO_CAPTION = "declineAutoCaption"
    }

}
