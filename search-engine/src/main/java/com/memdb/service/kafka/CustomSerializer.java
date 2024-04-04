package com.memdb.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.io.UncheckedIOException;

public class CustomSerializer implements Serializer<Object> {
    @Override
    public byte[] serialize(String topic, Object data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Serialization Error", e);

        }
    }
}

