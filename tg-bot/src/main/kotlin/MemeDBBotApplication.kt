package com.vitekkor.memeDB

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan("com.vitekkor.memeDB.config")
@SpringBootApplication
class MemeDBBotApplication

fun main(args: Array<String>) {
    runApplication<MemeDBBotApplication>(*args)
}
