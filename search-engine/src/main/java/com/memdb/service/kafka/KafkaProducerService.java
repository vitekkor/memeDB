package com.memdb.service.kafka;

import com.memdb.service.kafka.dto.CaptionCallbackDto;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessageToCaptionQueue(CaptionQueueDto message) {
        kafkaTemplate.send("caption_queue", message);
    }

    public void sendMessageToCaptionCallback(CaptionCallbackDto message) {
        kafkaTemplate.send("caption_callback", message);
    }
}

