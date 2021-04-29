package com.example.demo.db.repositories;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;

@Component
public class S3Repository {

    private static final Logger LOG = LoggerFactory.getLogger(S3Repository.class);

    private final Environment environment;

    private final AmazonS3 s3;

    @Autowired
    public S3Repository(final Environment environment) {
        this.environment = environment;
        this.s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    public void uploadFileToS3(final MultipartFile file, final Long id) {
        final ObjectMetadata fileMetadata = new ObjectMetadata();
        fileMetadata.setContentLength(file.getSize());
        fileMetadata.setContentType(file.getContentType());

        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream())) {
            final PutObjectRequest request = new PutObjectRequest(
                    environment.getProperty("bucketname"),
                    id.toString(),
                    bufferedInputStream,
                    fileMetadata);
            s3.putObject(request);
        } catch (IOException e) {
            LOG.error("IOException occurred in S3Repository: ", e);
            throw new RuntimeException(e);
        }
    }

    public S3Object getS3ObjectFromS3(final Long id) {
        return s3.getObject(environment.getProperty("bucketname"), id.toString());
    }
}
