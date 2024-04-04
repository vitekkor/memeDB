package service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import java.io.IOException
import java.io.UncheckedIOException

class CustomDeserializer : Deserializer<Any> {
    override fun deserialize(topic: String, data: ByteArray): Any {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.readValue(data, Any::class.java)
        } catch (e: IOException) {
            throw UncheckedIOException("Deserialization Error", e)
        }
    }
}
