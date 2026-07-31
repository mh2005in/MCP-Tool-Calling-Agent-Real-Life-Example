package com.immiauto.repository;

import com.immiauto.entity.RecruitmentEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentEvidenceRepository extends JpaRepository<RecruitmentEvidence, UUID> {
    List<RecruitmentEvidence> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
}
