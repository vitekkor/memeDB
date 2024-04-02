package com.memdb.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final MinioClient minioClient;

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
}
