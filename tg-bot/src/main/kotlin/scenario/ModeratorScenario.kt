package com.vitekkor.memeDB.scenario

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.callback
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.generic.and
import com.justai.jaicf.model.activation.onlyFrom
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.vitekkor.memeDB.misc.MediaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ModeratorScenario(
    private val mediaRepository: MediaRepository,
) : Scenario {
    private val whiteList = setOf("361863012", "422760400", "495768224", "458955823", "848702672")

    override val model: ScenarioModel = createModel {
        state("moderator") {
            activators { regex("/moderate") }
            action(telegram) {
                if (request.clientId in whiteList) {
                    mediaRepository.findAll(PageRequest.of(0, 10)).content.map {
                        reactions.sendPhoto(
                            it.fileId,
                            caption = it.description,
                            replyMarkup = InlineKeyboardMarkup.createSingleButton(
                                InlineKeyboardButton.CallbackData("DELETE", "DELETE ${it.fileId}")
                            )
                        )
                    }
                } else {
                    reactions.changeState("/")
                }
            }
            state("DELETE") {
                activators { regex("DELETE .*").onlyFrom(telegram.callback) }
                action(telegram.callback and com.justai.jaicf.activator.regex.regex) {
                    val messageId = request.message.messageId
                    val fileId = request.input.removePrefix("DELETE ")
                    mediaRepository.delete(mediaRepository.findMediaByFileId(fileId))
                    reactions.telegram?.run {
                        api.editMessageReplyMarkup(
                            chatId,
                            messageId,
                            replyMarkup = InlineKeyboardMarkup.create()
                        )
                    }
                    reactions.say("Ваш мем скоро будет добавлен в базу")
                }
            }
        }
    }
}
