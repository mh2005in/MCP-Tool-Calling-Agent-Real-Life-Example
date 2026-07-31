package com.immiauto.repository.forms;

import com.immiauto.entity.forms.PackageProfile;
import com.immiauto.enums.FormStatus;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PackageProfileRepository extends JpaRepository<PackageProfile, UUID> {

    Optional<PackageProfile> findByProfileCode(String profileCode);

    List<PackageProfile> findByStatus(FormStatus status);

    List<PackageProfile> findByServiceTypeAndStatus(ServiceType serviceType, FormStatus status);
}
