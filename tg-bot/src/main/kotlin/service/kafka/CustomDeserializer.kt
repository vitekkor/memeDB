package service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import service.kafka.dto.CaptionCallbackDto
import java.io.IOException
import java.io.UncheckedIOException


class CustomDeserializer : Deserializer<CaptionCallbackDto> {
    override fun deserialize(topic: String, data: ByteArray): CaptionCallbackDto {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.readValue(data, CaptionCallbackDto::class.java)
        } catch (e: IOException) {
            throw UncheckedIOException("Deserialization Error", e)
        }
    }
}

