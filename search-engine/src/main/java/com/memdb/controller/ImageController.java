package com.memdb.controller;

import com.memdb.model.Mem;
import com.memdb.model.MemDto;
import com.memdb.service.ElasticsearchService;
import com.memdb.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Slf4j
public class ImageController {
    private final ImageService imageService;
    private final ElasticsearchService elasticsearchService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(
            @RequestParam String description,
            @RequestPart(value = "file") MultipartFile file
    ) {
        log.info("Receive new image: {}", file.getContentType());
        var imageId = imageService.saveImage(file);
        if (imageId == null) {
            return ResponseEntity.internalServerError().body("Couldn't save image");
        }
        elasticsearchService.saveMem(imageId, "image", description);
        log.info("Image with description {} was saved. Id: {}", description, imageId);
        return ResponseEntity.ok().body(imageId);
    }

    @GetMapping(value = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<MemDto>> search(
            @RequestParam String description,
            @RequestParam(defaultValue = "5", required = false) Integer count
    ) {
        log.info("Searching top {} images by description: {}", count, description);
        List<Mem> memes = elasticsearchService.search(description, count);
        log.info("Found {} memes.", memes.size());
        return ResponseEntity.ok(memes.stream().map(MemDto::new).toList());
    }

    @DeleteMapping
    public ResponseEntity<String> delete(
            @RequestParam String id
    ) {
        log.info("Delete image by id: {}", id);
        elasticsearchService.delete(id);
        imageService.deleteImage(id);
        return ResponseEntity.ok("Ok");
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

    @GetMapping("/create_caption")
    public ResponseEntity<String> createCaption(
            @RequestPart(value = "file") MultipartFile file
    ) {
        log.info("Receive new imageId for creating caption");
        var imageId = imageService.saveImage(file);
        if (imageId == null) {
            log.error("Couldn't save image");
            return ResponseEntity.internalServerError().body("Couldn't save image");
        }
        var captionUUID = imageService.createCaption(imageId);
        return ResponseEntity.ok().body(captionUUID);
    }
}
