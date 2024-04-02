package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import org.springframework.stereotype.Component

@Component
class PingCommand : BaseCommand() {
    override val name: String = "ping"
    override val description: String = "Pong!"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { regex("/ping") }
        action {
            reactions.say("Pong!")
        }
    }
}