package service.kafka.dto;

import kotlinx.serialization.Serializable

@Serializable
data class CaptionCallbackDto(
    val id: String,
    val description: String,
    val status: String,
    val error: String
)
