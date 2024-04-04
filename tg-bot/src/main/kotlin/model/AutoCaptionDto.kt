package com.vitekkor.memeDB.model

import kotlinx.serialization.Serializable

@Serializable
data class AutoCaptionDto(
    val text: String,
    val replyMessageId: Long,
    val id: String
)
