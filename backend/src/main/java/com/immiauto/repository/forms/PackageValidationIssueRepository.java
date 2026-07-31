package com.immiauto.repository.forms;

import com.immiauto.entity.forms.PackageValidationIssue;
import com.immiauto.enums.ValidationSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageValidationIssueRepository extends JpaRepository<PackageValidationIssue, UUID> {

    List<PackageValidationIssue> findByCasePackageId(UUID casePackageId);

    List<PackageValidationIssue> findByCasePackageIdAndSeverity(UUID casePackageId, ValidationSeverity severity);

    List<PackageValidationIssue> findByCaseFormDraftId(UUID caseFormDraftId);

    long countByCasePackageIdAndSeverityAndResolvedFalse(UUID casePackageId, ValidationSeverity severity);
}
