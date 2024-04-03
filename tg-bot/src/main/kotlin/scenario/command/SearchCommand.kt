package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
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

            reactions.say(result)
            reactions.go("../../../")
        }
    }
}