package com.immiauto.repository.forms;

import com.immiauto.entity.forms.CasePackage;
import com.immiauto.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasePackageRepository extends JpaRepository<CasePackage, Long> {

    List<CasePackage> findByImmigrationCaseId(Long caseId);

    List<CasePackage> findByImmigrationCaseIdAndStatus(Long caseId, PackageStatus status);

    List<CasePackage> findByImmigrationCaseIdAndPackageProfileId(Long caseId, Long packageProfileId);
}
