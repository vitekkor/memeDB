package com.vitekkor.memeDB.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.model.scenario.getValue
import com.vitekkor.memeDB.scenario.command.BaseCommand
import org.springframework.stereotype.Component

@Component
class MainScenario(
    commands: List<BaseCommand>,
    autoCaptionScenario: AutoCaptionScenario,
    inlineQueryScenario: InlineQueryScenario,
) : Scenario {
    override val model: ScenarioModel by Scenario {
        state("start") {
            activators { regex("/start") }
            action(telegram) {
                reactions.say("Привет! Я memeDB бот - твой проводник в мире мемов. Введи команду /search для поиска")
                reactions.image("https://imgb.ifunny.co/images/c1215bcaf3141796cffde25cd16ca8bae3cce071ed536a948371ef207b33bbcd_1.webp")
            }
        }

        commands.forEach { append(it) }
        append(autoCaptionScenario)
        append(inlineQueryScenario)

        fallback {
            if (request.telegram?.message?.text.isNullOrEmpty()) {
                reactions.sayRandom(
                    "Простите, такой команды нет...",
                    "Возможно эта команда появится позже"
                )
            } else {
                reactions.go("/search")
            }
        }
    }
}
