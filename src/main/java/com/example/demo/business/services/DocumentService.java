package com.example.demo.business.services;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.demo.business.exceptions.DocumentNotFoundException;
import com.example.demo.business.models.Document;
import com.example.demo.data.entities.DocumentEntity;
import com.example.demo.data.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final JmsTemplate jmsTemplate;

    private final Environment environment;

    @Autowired
    public DocumentService(final DocumentRepository documentRepository,
                           final JmsTemplate jmsTemplate,
                           final Environment environment) {
        this.documentRepository = documentRepository;
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

    public void uploadFileToS3(final MultipartFile file) throws IOException {
        final AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withPathStyleAccessEnabled(true)
                .build();

        final ObjectMetadata fileMetadata = new ObjectMetadata();
        fileMetadata.setContentLength(file.getSize());
        fileMetadata.setContentType(file.getContentType());

        final PutObjectRequest request = new PutObjectRequest(
                environment.getProperty("bucketname"),
                file.getOriginalFilename(),
                file.getInputStream(),
                fileMetadata);

        s3Client.putObject(request);
    }

    public Document pushMessage(final String fileName, final String creator) {
        final Document message = createDocument(fileName, creator);
        jmsTemplate.convertAndSend(environment.getProperty("destination"), message);
        return message;
    }

    public S3Object getS3ObjectFromS3(final Long id) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
        return s3.getObject(environment.getProperty("bucketname"), getDocument(id).getContent());
    }

    private Optional<DocumentEntity> findDocumentEntityById(final Long id) {
        return documentRepository.findById(id);
    }
}
