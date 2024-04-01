package com.vitekkor.memeDB.model

import org.springframework.data.annotation.Id

data class SearchCommand(
    @Id
    val commandName: String,
    val text: String?
)
