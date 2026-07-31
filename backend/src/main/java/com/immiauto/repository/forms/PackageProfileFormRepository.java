package com.immiauto.repository.forms;

import com.immiauto.entity.forms.PackageProfileForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageProfileFormRepository extends JpaRepository<PackageProfileForm, UUID> {

    List<PackageProfileForm> findByPackageProfileIdOrderBySortOrder(UUID packageProfileId);
}
