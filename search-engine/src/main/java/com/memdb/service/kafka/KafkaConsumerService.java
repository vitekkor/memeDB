package com.memdb.service.kafka;

import com.memdb.service.CallbackService;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final CallbackService callbackService;

    @KafkaListener(topics = "caption_queue")
    public void receiveCaptionQueueDto(CaptionQueueDto captionQueueDto) {
        log.info("New message was received from topic 'caption_queue', id: {}", captionQueueDto.getId());
        callbackService.sendCallBack(captionQueueDto);
    }
}

