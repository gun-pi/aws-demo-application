package com.example.demo.business.models;

import com.example.demo.db.entities.DocumentEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Document implements Serializable {

    private Long id;

    private String creator;

    private String content;

    private LocalDateTime publishedOn;

    private byte[] file;

    public Document() {
    }

    public Document(final DocumentEntity documentEntity) {
        this.id = documentEntity.getId();
        this.creator = documentEntity.getCreator();
        this.content = documentEntity.getContent();
        this.publishedOn = documentEntity.getPublishedOn();
    }

    public Document(final byte[] file, final String creator) {
        this.file = file;
        this.creator = creator;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(final LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
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

    public byte[] getFile() {
        return file;
    }

    public void setFile(final byte[] file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", creator='" + creator + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
