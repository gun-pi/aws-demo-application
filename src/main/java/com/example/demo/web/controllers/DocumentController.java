package com.example.demo.web.controllers;

import com.example.demo.business.models.Document;
import com.example.demo.business.services.DocumentService;
import com.example.demo.web.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(final DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping(value = "/{id}")
    public DocumentDto getDocument(@PathVariable final Long id) {
        final Document document = documentService.getDocument(id);
        return new DocumentDto(document);
    }

    @PutMapping(headers = "Accept=application/json")
    public String saveDocument(@RequestBody final Document document) {
        final Long savedDocumentId = documentService.saveDocument(document);
        return String.format("Document (id=%s) was successfully uploaded", savedDocumentId);
    }

    @DeleteMapping(value = "/{id}")
    public String deleteDocument(@PathVariable final Long id) {
        documentService.deleteDocument(id);
        return String.format("Document (id=%s) is deleted", id);
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleException(final RuntimeException e) {
        return e.getMessage();
    }
}
