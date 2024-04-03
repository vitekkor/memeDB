package com.memdb.controller;

import com.memdb.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class ImageController implements ImageApi {
    private final ImageService imageService;

    @Override
    public ResponseEntity<String> imageGet(String imageId) {
        try {
            byte[] imageBytes = imageService.getImage(imageId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(Arrays.toString(imageBytes));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<String> imagePost(MultipartFile file) {
        var imageID = imageService.saveImage(file);
        return ResponseEntity.ok().body(imageID);
    }
}
