package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import org.springframework.stereotype.Component

@Component
class AddDescriptionCommand() : BaseCommand() {
    override val name: String = "addDescription"

    override val description: String = "добавление описания к мему"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        action {
            reactions.say("Добавление описания")

        }
    }
}