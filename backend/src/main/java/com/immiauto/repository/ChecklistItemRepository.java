package com.immiauto.repository;

import com.immiauto.entity.ChecklistItem;
import com.immiauto.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    List<ChecklistItem> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
    List<ChecklistItem> findByImmigrationCaseIdAndStatus(Long caseId, DocumentStatus status);
    long countByImmigrationCaseIdAndStatus(Long caseId, DocumentStatus status);
    long countByImmigrationCaseId(Long caseId);
}
