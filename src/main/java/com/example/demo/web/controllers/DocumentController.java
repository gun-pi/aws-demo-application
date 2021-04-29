package com.example.demo.web.controllers;

import com.example.demo.business.models.Document;
import com.example.demo.business.services.DocumentService;
import com.example.demo.web.dto.DocumentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @Autowired
    public DocumentController(final DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping(value = "/{id}")
    public DocumentDto getDocument(@PathVariable final Long id) {
        LOG.info("Getting the document with id: {}", id);
        final Document document = documentService.getDocument(id);
        return new DocumentDto(document);
    }

    @PutMapping(headers = "Accept=application/json")
    public String saveDocument(@RequestBody final Document document) {
        LOG.info("Saving the document: {}", document);
        final Long savedDocumentId = documentService.saveDocument(document);
        LOG.info("The document {} was saved with id {}", document, savedDocumentId);
        return String.format("Document (id=%s) was successfully uploaded", savedDocumentId);
    }

    @DeleteMapping(value = "/{id}")
    public String deleteDocument(@PathVariable final Long id) {
        LOG.info("Deleting the document with id: {}", id);
        documentService.deleteDocument(id);
        LOG.info("The document with id {} was deleted", id);
        return String.format("Document (id=%s) is deleted", id);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(final Exception e) {
        LOG.error("Exception occurred in DocumentController: ", e);
        return e.getMessage();
    }
}
