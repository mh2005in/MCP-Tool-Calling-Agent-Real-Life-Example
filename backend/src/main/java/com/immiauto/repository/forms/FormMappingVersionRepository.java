package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormMappingVersion;
import com.immiauto.enums.MappingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormMappingVersionRepository extends JpaRepository<FormMappingVersion, UUID> {

    List<FormMappingVersion> findByFormDefinitionIdOrderByMappingVersionDesc(UUID formDefinitionId);

    Optional<FormMappingVersion> findByFormDefinitionIdAndMappingVersion(UUID formDefinitionId, Integer mappingVersion);

    Optional<FormMappingVersion> findFirstByFormDefinitionIdAndStatusOrderByMappingVersionDesc(UUID formDefinitionId, MappingStatus status);
}
