package com.immiauto.repository.forms;

import com.immiauto.entity.forms.CaseFormDraft;
import com.immiauto.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseFormDraftRepository extends JpaRepository<CaseFormDraft, UUID> {

    List<CaseFormDraft> findByImmigrationCaseId(UUID caseId);

    List<CaseFormDraft> findByImmigrationCaseIdAndStatus(UUID caseId, PackageStatus status);

    List<CaseFormDraft> findByImmigrationCaseIdAndFormDefinitionId(UUID caseId, UUID formDefinitionId);
}
