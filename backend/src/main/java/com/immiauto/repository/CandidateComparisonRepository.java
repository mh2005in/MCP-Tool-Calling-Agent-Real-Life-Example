package com.immiauto.repository;

import com.immiauto.entity.CandidateComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateComparisonRepository extends JpaRepository<CandidateComparison, Long> {
    List<CandidateComparison> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
}
