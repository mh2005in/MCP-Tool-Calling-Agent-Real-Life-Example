package com.immiauto.repository;

import com.immiauto.entity.RecruitmentEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentEvidenceRepository extends JpaRepository<RecruitmentEvidence, Long> {
    List<RecruitmentEvidence> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
}
