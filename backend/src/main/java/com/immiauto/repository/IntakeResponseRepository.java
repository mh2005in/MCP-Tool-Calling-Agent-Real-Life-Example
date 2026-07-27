package com.immiauto.repository;

import com.immiauto.entity.IntakeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntakeResponseRepository extends JpaRepository<IntakeResponse, Long> {
    List<IntakeResponse> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
    List<IntakeResponse> findByImmigrationCaseIdAndSectionName(Long caseId, String sectionName);
}
