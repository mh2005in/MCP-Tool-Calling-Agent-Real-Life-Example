package com.immiauto.repository;

import com.immiauto.entity.IntakeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntakeResponseRepository extends JpaRepository<IntakeResponse, UUID> {
    List<IntakeResponse> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
    List<IntakeResponse> findByImmigrationCaseIdAndSectionName(UUID caseId, String sectionName);
}
