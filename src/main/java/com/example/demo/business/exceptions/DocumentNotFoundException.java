package com.example.demo.business.exceptions;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException() {
    }

    public DocumentNotFoundException(final String message) {
        super(message);
    }

    public DocumentNotFoundException(final Long id) {
        super(String.format("Document (id=%d) is not found", id));
    }
}
