package com.immiauto.repository.forms;

import com.immiauto.entity.forms.CasePackage;
import com.immiauto.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CasePackageRepository extends JpaRepository<CasePackage, UUID> {

    List<CasePackage> findByImmigrationCaseId(UUID caseId);

    List<CasePackage> findByImmigrationCaseIdAndStatus(UUID caseId, PackageStatus status);

    List<CasePackage> findByImmigrationCaseIdAndPackageProfileId(UUID caseId, UUID packageProfileId);
}
