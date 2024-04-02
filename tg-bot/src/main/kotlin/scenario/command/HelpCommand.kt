package com.vitekkor.memeDB.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import org.springframework.stereotype.Component

@Component
class HelpCommand(commands: MutableList<BaseCommand>) : BaseCommand() {

    override val name: String = "help"
    override val description: String = "список команд бота"

    private val commandsDescription = commands
        .apply { add(this@HelpCommand) }
        .sortedBy { it.name }
        .joinToString("\n") {
            "/${it.name} - ${it.description}"
        }

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { regex("/help") }
        action {
            reactions.say(commandsDescription)
        }
    }
}