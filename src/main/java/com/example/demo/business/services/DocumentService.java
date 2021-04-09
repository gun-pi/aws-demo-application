package com.example.demo.business.services;

import com.amazonaws.services.s3.model.S3Object;
import com.example.demo.business.exceptions.DocumentNotFoundException;
import com.example.demo.business.models.Document;
import com.example.demo.db.entities.DocumentEntity;
import com.example.demo.db.repositories.DocumentRepository;
import com.example.demo.db.repositories.S3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentService {

    private final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    private final S3Repository s3Repository;

    private final JmsTemplate jmsTemplate;

    private final Environment environment;

    @Autowired
    public DocumentService(final DocumentRepository documentRepository,
                           final S3Repository s3Repository,
                           final JmsTemplate jmsTemplate,
                           final Environment environment) {
        this.documentRepository = documentRepository;
        this.s3Repository = s3Repository;
        this.jmsTemplate = jmsTemplate;
        this.environment = environment;
    }

    public Document getDocument(final Long id) {
        DocumentEntity documentEntity = findDocumentEntityById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        return new Document(documentEntity);
    }

    public Document createDocument(final String content, final String creator) {
        final Document document = new Document();
        document.setContent(content);
        document.setCreator(creator);
        return new Document(documentRepository.save(new DocumentEntity(document)));
    }

    public Long saveDocument(final Document document) {
        if (document.getId() != null) {
            final Optional<DocumentEntity> savedDocumentEntity = findDocumentEntityById(document.getId());

            if (savedDocumentEntity.isPresent()) {
                return documentRepository.save(savedDocumentEntity.get().mergeDocuments(document)).getId();
            }
        }
        final DocumentEntity documentEntity = new DocumentEntity(document);
        return documentRepository.save(documentEntity).getId();
    }

    public void deleteDocument(final Long id) {
        DocumentEntity documentEntity = findDocumentEntityById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        documentRepository.delete(documentEntity);
    }

    public Long saveFile(final Document document) {
        return documentRepository.save(new DocumentEntity(document)).getId();
    }

    public byte[] getFile(final Long id) {
        return findDocumentEntityById(id).orElseThrow(
                () -> new DocumentNotFoundException(id)).getFile();
    }

    public Document getFileMetadata(final Long id) {
        final DocumentEntity documentEntity = findDocumentEntityById(id).orElseThrow(
                () -> new DocumentNotFoundException(id));

        return new Document(documentEntity);
    }

    public Document uploadFileToS3WithMessage(final MultipartFile file,
                                              final String fileName,
                                              final String creator) throws IOException {
        s3Repository.uploadFileToS3(file);
        LOG.info("Uploading file content {} to s3", fileName);

        final Document message = createDocument(fileName, creator);
        jmsTemplate.convertAndSend(environment.getProperty("destination"), message);
        LOG.info("Pushing document {} {} to message queue ", message.getContent(), message);

        return message;
    }

    public S3Object getS3ObjectFromS3(final Long id) {
        return s3Repository.getS3ObjectFromS3(id, getDocument(id).getContent());
    }

    private Optional<DocumentEntity> findDocumentEntityById(final Long id) {
        return documentRepository.findById(id);
    }
}
