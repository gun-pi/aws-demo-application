package com.example.demo.db.entities;

import com.example.demo.business.models.Document;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @SequenceGenerator(name = "seq", sequenceName = "oracle_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    @Column
    private String creator;

    @Column
    private String content;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private byte[] file;

    public DocumentEntity() {
    }

    public DocumentEntity(final Document document) {
        this.id = document.getId();
        this.creator = document.getCreator();
        this.content = document.getContent();
        this.file = document.getFile();
        this.publishedOn = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime();
    }

    public DocumentEntity mergeDocuments(final Document document) {
        if (document.getCreator() != null) {
            this.creator = document.getCreator();
        }

        if (document.getContent() != null) {
            this.content = document.getContent();
        }

        this.publishedOn = LocalDateTime.now();

        return this;
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
}
