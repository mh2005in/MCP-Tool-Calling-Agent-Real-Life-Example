package com.immiauto.repository;

import com.immiauto.entity.WorkHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistoryEntry, Long> {
    List<WorkHistoryEntry> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
}
