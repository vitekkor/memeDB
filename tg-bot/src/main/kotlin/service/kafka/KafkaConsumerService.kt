package service.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import service.kafka.dto.CaptionCallbackDto

@Service
class KafkaConsumerService {

    @KafkaListener(topics = ["caption_queue"])
    fun receiveCaptionQueueDto(captionQueueDto: CaptionCallbackDto?) {
        println(captionQueueDto)
    }
}

