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
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component


@Component
class ModeratorScenario(
    private val mongoTemplate: MongoTemplate,
) : Scenario {
    private val whiteList = setOf("361863012", "422760400", "495768224", "458955823", "848702672")

    override val model: ScenarioModel = createModel {
        state("moderator") {
            activators { regex("/moderate") }
            action(telegram) {
                if (request.clientId in whiteList) {
                    val photos = findDocumentsWithoutIdAnnotation().map {
                        reactions.sendPhoto(
                            it.getString("fileId"),
                            caption = it.getString("description"),
                            replyMarkup = InlineKeyboardMarkup.createSingleButton(
                                InlineKeyboardButton.CallbackData(
                                    "DELETE",
                                    "DELETE ${it.getObjectId("_id")}"
                                )
                            )
                        )
                    }.size
                    if (photos == 0) {
                        reactions.say("No memes")
                    }
                } else {
                    reactions.changeState("/")
                }
            }
            state("DELETE") {
                activators { regex("DELETE .*").onlyFrom(telegram.callback) }
                action(telegram.callback and com.justai.jaicf.activator.regex.regex) {
                    val messageId = request.message.messageId
                    val id = request.input.removePrefix("DELETE ")
                    deleteById(id)

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

    fun findDocumentsWithoutIdAnnotation(): List<Document> {
        val query = Query().limit(10)
        val documents: List<Document> = mongoTemplate.find(query, Document::class.java, "media")
        return documents
    }

    fun deleteById(id: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        mongoTemplate.remove(query, Document::class.java, "media")
    }
}
