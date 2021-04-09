package com.example.demo.db.repositories;

import com.example.demo.db.entities.DocumentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {
}
