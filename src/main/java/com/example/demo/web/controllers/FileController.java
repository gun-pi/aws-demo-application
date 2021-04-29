package com.example.demo.web.controllers;

import com.amazonaws.services.s3.model.S3Object;
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
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final DocumentService documentService;

    @Autowired
    public FileController(final DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public String saveFile(@RequestParam("file") MultipartFile file,
                           @RequestParam("creator") String creator) throws IOException {
        LOG.info("Uploading file {}. Content type: {}. Size: {}", file.getOriginalFilename(),
                file.getContentType(), file.getSize());
        final Document document = documentService.uploadFileToS3WithMessage(file, creator);
        return String.format("File with id %s is uploading", document.getId());
    }

    @GetMapping(value = "/{id}/content")
    public DeferredResult<ResponseEntity<InputStreamResource>> getFile(@PathVariable Long id) {
        LOG.info("Getting the file with id: {}", id);
        DeferredResult<ResponseEntity<InputStreamResource>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            try {
                final S3Object object = documentService.getS3ObjectFromS3(id);
                output.setResult(ResponseEntity.ok()
                        .contentLength(object.getObjectMetadata().getContentLength())
                        .contentType(MediaType.parseMediaType(object.getObjectMetadata().getContentType()))
                        .body(new InputStreamResource(new BufferedInputStream(object.getObjectContent()))));
            } catch (Exception e) {
                output.setErrorResult(e);
            }
        });
        return output;
    }

    @GetMapping(value = "/{id}/metadata")
    public FileMetadataDto getFileMetadata(@PathVariable Long id) {
        LOG.info("Getting the metadata of the file with id: {}", id);
        final Document document = documentService.getFileMetadata(id);
        return new FileMetadataDto(document);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(final Exception e) {
        LOG.error("Exception occurred in FileController: ", e);
        return e.getMessage();
    }
}
