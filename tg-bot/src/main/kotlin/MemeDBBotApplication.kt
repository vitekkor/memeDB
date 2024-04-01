package com.vitekkor.memeDB

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationProperties
@SpringBootApplication
class MemeDBBotApplication

fun main(args: Array<String>) {
    runApplication<MemeDBBotApplication>(*args)
}