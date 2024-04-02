package com.vitekkor.memeDB.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "bot")
data class BotConfigurationProperties(val telegramToken: String, val mongoCollection: String)