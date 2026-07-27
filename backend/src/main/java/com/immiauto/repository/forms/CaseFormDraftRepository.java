package com.immiauto.repository.forms;

import com.immiauto.entity.forms.CaseFormDraft;
import com.immiauto.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseFormDraftRepository extends JpaRepository<CaseFormDraft, Long> {

    List<CaseFormDraft> findByImmigrationCaseId(Long caseId);

    List<CaseFormDraft> findByImmigrationCaseIdAndStatus(Long caseId, PackageStatus status);

    List<CaseFormDraft> findByImmigrationCaseIdAndFormDefinitionId(Long caseId, Long formDefinitionId);
}
