package com.memdb.service.kafka;

import com.memdb.service.CallbackService;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final CallbackService callbackService;

    @KafkaListener(topics = "caption_queue")
    public void receiveCaptionQueueDto(CaptionQueueDto captionQueueDto) {
        callbackService.sendCallBack(captionQueueDto);
    }
}

