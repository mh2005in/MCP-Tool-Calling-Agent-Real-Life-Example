package com.immiauto.repository;

import com.immiauto.entity.Document;
import com.immiauto.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByImmigrationCaseId(UUID caseId);
    List<Document> findByImmigrationCaseIdAndStatus(UUID caseId, DocumentStatus status);
}
