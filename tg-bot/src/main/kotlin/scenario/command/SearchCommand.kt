package com.vitekkor.memeDB.scenario.command

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaVideo
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.helpers.logging.logger
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.memeDB.scenario.extension.sendPhoto
import com.vitekkor.memeDB.scenario.extension.sendVideo
import com.vitekkor.memeDB.service.searchcommand.SearchCommandService
import org.springframework.stereotype.Component

@Component
class SearchCommand(private val searchCommandService: SearchCommandService) : BaseCommand() {
    override val name: String = "search"
    override val description: String = "поиск мемов по описанию"
    private val images = listOf(
        "https://cs9.pikabu.ru/post_img/2016/10/26/6/og_og_1477473794223474155.jpg",
        "https://avatars.dzeninfra.ru/get-zen_doc/1534997/pub_5ccd9b67ffaa2300b352e32a_5ccda7554900f400af337e70/scale_1200",
        "https://cdn1.flamp.ru/64780ce1ec27b308d068e45fb2eb3527_1920.jpg",
        )

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { regex("/search(\\s+(?<searchText>.*))?") }

        action {
            val searchText = activator.regex?.group("searchText") ?: kotlin.run {
                reactions.sayRandom(
                    "Какой мем хочешь найти?",
                    "Ничего не понятно, но очень интересно. Пожалуйста, введите текст запроса"
                )
                return@action
            }

            val result = kotlin.runCatching { searchCommandService.findMemes(searchText) }.onFailure {
                logger.error("Error", it)
            }.getOrNull()
            if (result == null) {
                reactions.telegram?.sendPhoto(
                    images.random(),
                    caption = "Извините, произошла ошибка. Попробуйте заново"
                )
                return@action
            }
            when (result.size) {
                0 -> reactions.say("К сожалению ничего не нашлось \uD83D\uDE14 Попробуйте другой запрос")
                1 -> {
                    val media = result.first()
                    when (media.second) {
                        "image" -> reactions.telegram?.sendPhoto(media.first)
                        "video" -> reactions.telegram?.sendVideo(media.first)
                        else -> reactions.telegram?.sendPhoto(
                            images.random(),
                            caption = "Извините, произошла ошибка. Попробуйте заново"
                        )
                    }
                }

                else -> reactions.telegram?.sendMediaGroup(
                    MediaGroup.from(
                        *result.mapNotNull { media ->
                            when (media.second) {
                                "image" -> InputMediaPhoto(TelegramFile.ByFile(media.first))
                                "video" -> InputMediaVideo(TelegramFile.ByFile(media.first))
                                else -> null
                            }
                        }.toTypedArray()
                    )
                )
            }
            reactions.say("Найти что-нибудь ещё?")
            reactions.go("../../../")
        }
    }
}
