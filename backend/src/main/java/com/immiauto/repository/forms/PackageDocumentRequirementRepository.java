package com.immiauto.repository.forms;

import com.immiauto.entity.forms.PackageDocumentRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageDocumentRequirementRepository extends JpaRepository<PackageDocumentRequirement, Long> {

    List<PackageDocumentRequirement> findByPackageProfileIdOrderBySortOrder(Long packageProfileId);
}
