package com.memdb.controller;

import com.memdb.model.Mem;
import com.memdb.service.ElasticsearchService;
import com.memdb.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;
    private final ElasticsearchService elasticsearchService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(
            @RequestParam String description,
            @RequestPart(value = "file") MultipartFile file
    ) {

        var imageId = imageService.saveImage(file);
        elasticsearchService.saveMem(imageId, file.getContentType(), description);
        return ResponseEntity.ok().body(imageId);
    }

    @GetMapping
    public ResponseEntity<List<String>> search(
            @RequestParam String description,
            @RequestParam(defaultValue = "5", required = false) Integer count
    ) {
        List<Mem> mem = elasticsearchService.search(description, count);
        return ResponseEntity.ok(mem.stream().map(Mem::getUuid).toList());
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String imageId
    ) {
        try {
            byte[] imageBytes = imageService.getImage(imageId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType mapType(String type) {
        return switch (type) {
            case "image/jpeg" -> MediaType.IMAGE_JPEG;
            case "image/gif" -> MediaType.IMAGE_GIF;
            case "image/png" -> MediaType.IMAGE_PNG;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

}
