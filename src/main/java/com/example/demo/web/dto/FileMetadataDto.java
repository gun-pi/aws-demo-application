package com.example.demo.web.dto;

import com.example.demo.business.models.Document;

import java.time.LocalDateTime;

public class FileMetadataDto {

    private Long id;

    private String creator;

    private LocalDateTime publishedOn;

    public FileMetadataDto() {
    }

    public FileMetadataDto(Document document) {
        this.id = document.getId();
        this.creator = document.getCreator();
        this.publishedOn = document.getPublishedOn();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(final LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }
}
