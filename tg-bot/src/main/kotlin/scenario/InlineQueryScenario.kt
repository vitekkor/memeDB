package com.vitekkor.memeDB.scenario

import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.helpers.logging.logger
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.vitekkor.memeDB.misc.CustomTelegramEvent
import com.vitekkor.memeDB.misc.FileIdRepository
import com.vitekkor.memeDB.misc.TelegramInlineQueryRequest
import com.vitekkor.memeDB.service.searchcommand.SearchCommandService
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class InlineQueryScenario(
    private val searchCommandService: SearchCommandService,
    private val fileIdRepository: FileIdRepository,
) : Scenario {
    private val log = KotlinLogging.logger {}
    override val model: ScenarioModel = createModel {
        state(CustomTelegramEvent.INLINE_QUERY_EVENT) {
            activators { event(CustomTelegramEvent.INLINE_QUERY_EVENT) }
            action(telegram) {
                val telegramInlineQueryRequest = (request as TelegramInlineQueryRequest)
                val result = kotlin.runCatching {
                    searchCommandService.findMemesIds(telegramInlineQueryRequest.query)
                }.onFailure {
                    logger.error("Error", it)
                }.getOrNull()
                log.info("Found memes {}", result)
                if (result == null) {
                    reactions.api.answerInlineQuery(telegramInlineQueryRequest.id)
                    return@action
                }
                val fileIds = fileIdRepository.findAllById(result.map { it.id })
                log.info("Found files {}", fileIds)
                val answer = fileIds.mapNotNull { (fileId, id, type) ->
                    when (type) {
                        "image" -> InlineQueryResult.CachedPhoto(id, fileId)
                        "video" -> InlineQueryResult.CachedVideo(id, fileId, "")
                        else -> null
                    }
                }
                log.info("Answer {}", answer)
                reactions.api.answerInlineQuery(telegramInlineQueryRequest.id, answer)
                reactions.goBack()
            }
        }
    }
}
