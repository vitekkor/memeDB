package com.memdb.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.io.UncheckedIOException;

public class CustomDeserializer implements Deserializer<Object> {
    @Override
    public Object deserialize(String topic, byte[] data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(data, CaptionQueueDto.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Deserialization Error", e);
        }
    }
}
