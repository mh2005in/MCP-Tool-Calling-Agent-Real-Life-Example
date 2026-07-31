package com.immiauto.repository;

import com.immiauto.entity.CandidateComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateComparisonRepository extends JpaRepository<CandidateComparison, UUID> {
    List<CandidateComparison> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
}
