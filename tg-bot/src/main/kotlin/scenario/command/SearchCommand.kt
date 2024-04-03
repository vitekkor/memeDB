package com.vitekkor.memeDB.scenario.command

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.memeDB.scenario.extension.sendPhoto
import com.vitekkor.memeDB.service.searchcommand.SearchCommandService
import org.springframework.stereotype.Component

@Component
class SearchCommand(private val searchCommandService: SearchCommandService) : BaseCommand() {
    override val name: String = "search"
    override val description: String = "поиск мемов по описанию"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { regex("/search\\s+(?<searchText>.*)") }

        action {
            val searchText = activator.regex?.group("searchText") ?: return@action

            val result = searchCommandService.findMemes(searchText)

            when (result.size) {
                0 -> reactions.say("К сожалению ничего не нашлось \uD83D\uDE14. Попробуйте другой запрос.")
                1 -> reactions.telegram?.sendPhoto(result.first())
                else -> reactions.telegram?.sendMediaGroup(
                    MediaGroup.from(
                        *result.map {
                            InputMediaPhoto(TelegramFile.ByFile(it))
                        }.toTypedArray()
                    )
                )
            }
            reactions.go("../../../")
        }
    }
}
