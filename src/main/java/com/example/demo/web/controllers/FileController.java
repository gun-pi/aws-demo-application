package com.example.demo.web.controllers;

import com.amazonaws.services.s3.model.S3Object;
import com.example.demo.business.exceptions.DocumentNotFoundException;
import com.example.demo.business.models.Document;
import com.example.demo.business.services.DocumentService;
import com.example.demo.web.dto.FileMetadataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    private final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final DocumentService documentService;

    @Autowired
    public FileController(final DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/file")
    public String saveFile(@RequestParam("file") MultipartFile file,
                           @RequestParam("creator") String creator) throws IOException {
        LOG.info("Uploading file {}. Content type: {}. Size: {}", file.getOriginalFilename(),
                file.getContentType(), file.getSize());

        documentService.uploadFileToS3(file);
        LOG.info("Uploading file content {} to s3", file.getOriginalFilename());

        final Document message = documentService.pushMessage(file.getOriginalFilename(), creator);
        LOG.info("Pushing document {} {} to message queue ", message.getContent(), message);

        return String.format("File %s is uploading", message.getContent());
    }

    @GetMapping(value = "/file/{id}/content")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Long id) {
        final S3Object object = documentService.getS3ObjectFromS3(id);
        return ResponseEntity.ok()
                .contentLength(object.getObjectMetadata().getContentLength())
                .contentType(MediaType.parseMediaType(object.getObjectMetadata().getContentType()))
                .body(new InputStreamResource(object.getObjectContent()));
    }

    @GetMapping(value = "/file/{id}/metadata")
    public FileMetadataDto getFileMetadata(@PathVariable Long id) {
        final Document document = documentService.getFileMetadata(id);
        return new FileMetadataDto(document);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public String handleException(final DocumentNotFoundException e) {
        return e.getMessage();
    }
}
