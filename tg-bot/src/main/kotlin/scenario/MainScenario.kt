package com.vitekkor.memeDB.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.model.scenario.getValue
import org.springframework.stereotype.Component

@Component
class MainScenario() : Scenario {
    override val model: ScenarioModel by Scenario {
        state("start") {
            activators { regex("/start") }
            action(telegram) {
                reactions.say("Привет!")
            }
        }
    }
}