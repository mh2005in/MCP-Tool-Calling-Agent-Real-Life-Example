package com.immiauto.repository;

import com.immiauto.entity.ChecklistItem;
import com.immiauto.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {
    List<ChecklistItem> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
    List<ChecklistItem> findByImmigrationCaseIdAndStatus(UUID caseId, DocumentStatus status);
    long countByImmigrationCaseIdAndStatus(UUID caseId, DocumentStatus status);
    long countByImmigrationCaseId(UUID caseId);
}
