package com.immiauto.repository;

import com.immiauto.entity.Document;
import com.immiauto.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByImmigrationCaseId(Long caseId);
    List<Document> findByImmigrationCaseIdAndStatus(Long caseId, DocumentStatus status);
}
