package com.example.demo.web.dto;

import com.example.demo.business.models.Document;

import java.time.LocalDateTime;

public class DocumentDto {

    private Long id;

    private String creator;

    private String content;

    private LocalDateTime publishedOn;

    public DocumentDto() {
    }

    public DocumentDto(Document document) {
        this.id = document.getId();
        this.creator = document.getCreator();
        this.content = document.getContent();
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

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(final LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }
}
