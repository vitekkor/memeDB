package com.vitekkor.memeDB.model

import kotlinx.serialization.Serializable

@Serializable
data class MemDto(
    val id: String,
    val type: String
)
