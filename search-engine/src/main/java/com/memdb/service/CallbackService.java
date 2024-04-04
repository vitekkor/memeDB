package com.memdb.service;

import com.memdb.service.kafka.KafkaProducerService;
import com.memdb.service.kafka.dto.CaptionCallbackDto;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import com.memdb.service.kafka.dto.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallbackService {
    private final ImageService imageService;
    private final KafkaProducerService kafkaProducerService;
    private final static String DESCRIPTION = "DESCRIPTION";

    public void sendCallBack(CaptionQueueDto captionQueueDto) {
        CaptionCallbackDto callbackDto = new CaptionCallbackDto();
        try {
            imageService.getImage(captionQueueDto.getMediaId());
            callbackDto.setId(callbackDto.getId());
            callbackDto.setStatus(StatusType.DONE);
            callbackDto.setDescription(DESCRIPTION);
        } catch (Exception e) {
            callbackDto.setId(callbackDto.getId());
            callbackDto.setStatus(StatusType.FAILED);
            callbackDto.setError(e.getMessage());
        } finally {
            kafkaProducerService.sendMessageToCaptionCallback(callbackDto);
        }
    }
}
