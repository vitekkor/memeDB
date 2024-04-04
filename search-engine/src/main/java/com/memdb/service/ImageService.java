package com.memdb.service;

import com.memdb.service.kafka.KafkaProducerService;
import com.memdb.service.kafka.dto.CaptionQueueDto;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final MinioClient minioClient;
    private final KafkaProducerService kafkaProducerService;

    @Value("${minio.bucketName}")
    private String bucketName;

    public String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString();
            InputStream inputStream = file.getInputStream();
            var response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, -1, 10485760)
                    .build());
            return response.object();
        } catch (Exception e) {
            return "Error saving image";
        }
    }

    public byte[] getImage(String imageId) {
        try {
            InputStream imageStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageId)
                    .build());

            return IOUtils.toByteArray(imageStream);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving image from MinIO");
        }
    }

    public void deleteImage(String id) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving image from MinIO");
        }
    }

    public String createCaption(String imageId) {
        var captionUUID = (UUID.randomUUID().toString());
        var captionDto = new CaptionQueueDto();
        captionDto.setId(captionUUID);
        captionDto.setMediaId(imageId);
        kafkaProducerService.sendMessageToCaptionQueue(captionDto);
        return captionUUID;
    }
}
