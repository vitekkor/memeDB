package com.memdb.service.kafka;

import com.memdb.service.kafka.dto.CaptionCallbackDto;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessageToCaptionQueue(CaptionQueueDto message) {
        kafkaTemplate.send("caption_queue", message);
        log.info("New message was send to topic 'caption_queue', id: {}", message.getId());
    }

    public void sendMessageToCaptionCallback(CaptionCallbackDto message) {
        kafkaTemplate.send("caption_callback", message);
        log.info("New message was send to topic 'caption_callback', id: {}", message.getId());
    }
}

