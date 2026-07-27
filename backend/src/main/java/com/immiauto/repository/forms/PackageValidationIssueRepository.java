package com.immiauto.repository.forms;

import com.immiauto.entity.forms.PackageValidationIssue;
import com.immiauto.enums.ValidationSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageValidationIssueRepository extends JpaRepository<PackageValidationIssue, Long> {

    List<PackageValidationIssue> findByCasePackageId(Long casePackageId);

    List<PackageValidationIssue> findByCasePackageIdAndSeverity(Long casePackageId, ValidationSeverity severity);

    List<PackageValidationIssue> findByCaseFormDraftId(Long caseFormDraftId);

    long countByCasePackageIdAndSeverityAndResolvedFalse(Long casePackageId, ValidationSeverity severity);
}
