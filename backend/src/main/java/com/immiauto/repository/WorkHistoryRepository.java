package com.immiauto.repository;

import com.immiauto.entity.WorkHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistoryEntry, UUID> {
    List<WorkHistoryEntry> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
}
